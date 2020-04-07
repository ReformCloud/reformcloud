package systems.reformcloud.reformcloud2.executor.node.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.NetworkInfo;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.detail.ProcessDetail;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.util.MemoryCalculator;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.node.process.NodeProcessManager;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventProcessUpdated;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.PacketOutHeadNodeStartProcess;
import systems.reformcloud.reformcloud2.executor.node.process.basic.BasicLocalNodeProcess;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;
import systems.reformcloud.reformcloud2.executor.node.process.startup.LocalProcessQueue;
import systems.reformcloud.reformcloud2.executor.node.util.ProcessCopyOnWriteArrayList;

import java.util.*;
import java.util.stream.Collectors;

public final class LocalNodeProcessManager implements NodeProcessManager {

    private final Collection<ProcessInformation> information = Collections.synchronizedCollection(new ProcessCopyOnWriteArrayList());

    @Nullable
    @Override
    public ProcessInformation getLocalCloudProcess(@NotNull String name) {
        return Streams.filterToReference(information, e -> e.getProcessDetail().getName().equals(name) && isLocal(e.getProcessDetail().getProcessUniqueID())).orNothing();
    }

    @Nullable
    @Override
    public ProcessInformation getLocalCloudProcess(@NotNull UUID uuid) {
        return Streams.filterToReference(information, e -> e.getProcessDetail().getProcessUniqueID().equals(uuid) && isLocal(e.getProcessDetail().getProcessUniqueID())).orNothing();
    }

    @NotNull
    @Override
    public synchronized ProcessInformation prepareLocalProcess(@NotNull ProcessConfiguration configuration,
                                                               @NotNull Template template, boolean start) {
        return this.prepareLocalProcess(this.constructInfo(
                configuration, template, NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode()
        ), start);
    }

    @NotNull
    @Override
    public synchronized ProcessInformation prepareLocalProcess(@NotNull ProcessInformation processInformation, boolean start) {
        processInformation.toWrapped().acceptAndUpdate(
                e -> e.getNetworkInfo().setHost(NodeExecutor.getInstance().getNodeConfig().getStartHost())
        );

        this.handleProcessStart(processInformation);

        NodeInformation information = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode();
        information.addUsedMemory(processInformation.getProcessDetail().getMaxMemory());
        NodeExecutor.getInstance().getClusterSyncManager().syncSelfInformation();

        if (start) {
            LocalProcessQueue.queue(processInformation);
        } else {
            RunningProcess process = new BasicLocalNodeProcess(processInformation);
            process.prepare();
        }

        return processInformation;
    }

    @Nullable
    @Override
    public ProcessInformation stopLocalProcess(@NotNull String name) {
        List<RunningProcess> processes = LocalProcessManager.getNodeProcesses()
                .stream()
                .filter(e -> e.getProcessInformation().getProcessDetail().getName().equals(name))
                .collect(Collectors.toList());
        if (processes.isEmpty()) {
            return null;
        }

        processes.forEach(RunningProcess::shutdown);
        return processes.get(0).getProcessInformation();
    }

    @Nullable
    @Override
    public ProcessInformation stopLocalProcess(@NotNull UUID uuid) {
        List<RunningProcess> processes = LocalProcessManager.getNodeProcesses()
                .stream()
                .filter(e -> e.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(uuid))
                .collect(Collectors.toList());
        if (processes.isEmpty()) {
            return null;
        }

        processes.forEach(RunningProcess::shutdown);
        return processes.get(0).getProcessInformation();
    }

    @NotNull
    @Override
    public synchronized ProcessInformation queueProcess(@NotNull ProcessConfiguration configuration, @NotNull Template template,
                                                        @NotNull NodeInformation node, boolean start) {
        ProcessInformation processInformation = constructInfo(configuration, template, node);
        this.handleProcessStart(processInformation);

        DefaultChannelManager.INSTANCE.get(node.getName()).ifPresent(e -> e.sendPacket(new PacketOutHeadNodeStartProcess(
                processInformation, start
        )));
        return processInformation;
    }

    @Override
    public void registerLocalProcess(@NotNull RunningProcess process) {
        this.information.add(process.getProcessInformation());
    }

    @Override
    public void unregisterLocalProcess(@NotNull UUID uniqueID) {
        Streams.filterToReference(
                information,
                e -> e.getProcessDetail().getProcessUniqueID().equals(uniqueID)
        ).ifPresent(information::remove);
    }

    @Override
    public void handleLocalProcessStart(@NotNull ProcessInformation processInformation) {
        this.handleProcessStart(processInformation);

        NodeInformation information = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode();
        information.getStartedProcesses().add(new NodeProcess(
                processInformation.getProcessGroup().getName(),
                processInformation.getProcessDetail().getName(),
                processInformation.getProcessDetail().getProcessUniqueID()
        ));

        NodeExecutor.getInstance().getClusterSyncManager().syncSelfInformation();
    }

    @Override
    public void handleLocalProcessStop(@NotNull ProcessInformation processInformation) {
        this.handleProcessStop(processInformation);

        NodeInformation information = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode();
        information.removeUsedMemory(processInformation.getProcessDetail().getMaxMemory());
        Streams.filterToReference(
                information.getStartedProcesses(),
                e -> e.getUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID())
        ).ifPresent(information.getStartedProcesses()::remove);

        NodeExecutor.getInstance().getClusterSyncManager().syncSelfInformation();
    }

    @Override
    public void handleProcessStart(@NotNull ProcessInformation processInformation) {
        ProcessInformation information = Streams.filterToReference(
                this.information,
                e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID())
        ).orNothing();
        if (information == null) {
            this.information.add(processInformation);
            return;
        }

        this.information.remove(information);
        this.information.add(processInformation);
    }

    @Override
    public void handleProcessUpdate(@NotNull ProcessInformation processInformation) {
        Streams.filterToReference(
                this.information,
                e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID())
        ).ifPresent(e -> {
            this.information.remove(e);
            this.information.add(processInformation);
        });
    }

    @Override
    public void handleProcessConnection(@NotNull ProcessInformation processInformation) {
        Task.EXECUTOR.execute(() -> {
            while (!DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).isPresent()) {
                AbsoluteThread.sleep(5);
            }

            DefaultChannelManager.INSTANCE.get(processInformation.getProcessDetail().getName()).ifPresent(
                    e -> e.sendPacket(new ControllerEventProcessUpdated(processInformation))
            );
        });
    }

    @Override
    public void handleProcessStop(@NotNull ProcessInformation processInformation) {
        Streams.filterToReference(
                this.information,
                e -> e.getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID())
        ).ifPresent(information::remove);
    }

    @Override
    public void handleProcessDisconnect(@NotNull String name) {
        ProcessInformation information = this.getLocalCloudProcess(name);
        if (information == null) {
            return;
        }

        this.information.remove(information);
        this.removeProcess(information);
    }

    @Override
    public boolean isLocal(@NotNull String name) {
        return Streams.filterToReference(
                this.information,
                e -> e.getProcessDetail().getName().equals(name)
                        && e.getProcessDetail().getParentUniqueID().equals(
                        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getNodeUniqueID()
                )
        ).isPresent();
    }

    @Override
    public boolean isLocal(@NotNull UUID uniqueID) {
        return Streams.filterToReference(
                information,
                e -> e.getProcessDetail().getProcessUniqueID().equals(uniqueID)
                        && e.getProcessDetail().getParentUniqueID().equals(
                        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getNodeUniqueID()
                )
        ).isPresent();
    }

    @NotNull
    @Override
    public Collection<ProcessInformation> getClusterProcesses() {
        return this.information;
    }

    @NotNull
    @Override
    public Collection<ProcessInformation> getClusterProcesses(@NotNull String group) {
        return Streams.allOf(this.information, e -> e.getProcessGroup().getName().equals(group));
    }

    @NotNull
    @Override
    public Collection<ProcessInformation> getLocalProcesses() {
        return Streams.allOf(
                this.getClusterProcesses(),
                e -> e.getProcessDetail().getParentUniqueID().equals(
                        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getNodeUniqueID()
                )
        );
    }

    @Nullable
    @Override
    public ProcessInformation getClusterProcess(@NotNull String name) {
        return Streams.filterToReference(
                this.information,
                e -> e.getProcessDetail().getName().equals(name)
        ).orNothing();
    }

    @Nullable
    @Override
    public ProcessInformation getClusterProcess(@NotNull UUID uniqueID) {
        return Streams.filterToReference(
                this.information,
                e -> e.getProcessDetail().getProcessUniqueID().equals(uniqueID)
        ).orNothing();
    }

    @NotNull
    @Override
    public Iterator<ProcessInformation> iterator() {
        return Streams.newList(information).iterator();
    }

    @Override
    public void update(@NotNull ProcessInformation processInformation) {
        this.handleProcessUpdate(processInformation);
        NodeExecutor.getInstance().getClusterSyncManager().syncProcessUpdate(processInformation);
    }

    private void removeProcess(ProcessInformation information) {
        NodeProcess nodeProcess = Streams.filter(
                NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getStartedProcesses(),
                e -> e.getUniqueID().equals(information.getProcessDetail().getProcessUniqueID())
        );
        if (nodeProcess == null) {
            return;
        }

        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getStartedProcesses().remove(nodeProcess);
        NodeExecutor.getInstance().getClusterSyncManager().syncProcessStop(information);
    }

    private synchronized int nextID(ProcessGroup processGroup) {
        int id = 1;
        Collection<Integer> ids = Streams.newCollection(
                this.information,
                processInformation -> processInformation.getProcessGroup().getName().equals(processGroup.getName()),
                e -> e.getProcessDetail().getId()
        );

        while (ids.contains(id)) {
            id++;
        }

        return id;
    }

    private synchronized int nextPort(int startPort) {
        Collection<Integer> ports = Streams.newCollection(
                this.information,
                ignored -> true,
                e -> e.getNetworkInfo().getPort()
        );

        while (ports.contains(startPort)) {
            startPort++;
        }

        return startPort;
    }

    @NotNull
    private synchronized ProcessInformation constructInfo(@NotNull ProcessConfiguration configuration, @NotNull Template template,
                                                          @NotNull NodeInformation node) {
        int id = configuration.getId() == -1
                ? this.nextID(configuration.getBase())
                : configuration.getId();

        int port = configuration.getPort() == null
                ? nextPort(configuration.getBase().getStartupConfiguration().getStartPort())
                : configuration.getPort();

        UUID uniqueID = configuration.getUniqueId();

        String displayName = configuration.getDisplayName();
        if (displayName == null) {
            StringBuilder stringBuilder = new StringBuilder().append(configuration.getBase().getName());
            if (configuration.getBase().isShowIdInName()) {
                if (template.getServerNameSplitter() != null) {
                    stringBuilder.append(template.getServerNameSplitter());
                }

                stringBuilder.append(id);
            }

            displayName = stringBuilder.toString();
        }

        for (ProcessInformation allProcess : this.getClusterProcesses()) {
            if (allProcess.getProcessDetail().getId() == id) {
                id = nextID(configuration.getBase());
            }

            if (allProcess.getNetworkInfo().getPort() == port) {
                port = nextPort(configuration.getBase().getStartupConfiguration().getStartPort());
            }

            if (allProcess.getProcessDetail().getProcessUniqueID().equals(uniqueID)) {
                uniqueID = UUID.randomUUID();
            }

            if (allProcess.getProcessDetail().getDisplayName().equals(displayName)) {
                displayName += UUID.randomUUID().toString().split("-")[0];
            }
        }

        ProcessInformation processInformation = new ProcessInformation(
                new ProcessDetail(
                        uniqueID,
                        node.getNodeUniqueID(),
                        node.getName(),
                        configuration.getBase().getName() + template.getServerNameSplitter() + id,
                        displayName,
                        id,
                        template,
                        configuration.getMaxMemory() == null
                                ? MemoryCalculator.calcMemory(configuration.getBase().getName(), template)
                                : configuration.getMaxMemory()
                ),
                new NetworkInfo(
                        NodeExecutor.getInstance().getNodeConfig().getStartHost(),
                        port
                ), configuration.getBase(), configuration.getExtra(), configuration.getInclusions()
        );

        return processInformation.updateMaxPlayers(null);
    }
}