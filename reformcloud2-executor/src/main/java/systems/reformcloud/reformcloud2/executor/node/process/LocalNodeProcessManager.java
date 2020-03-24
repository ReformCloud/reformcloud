package systems.reformcloud.reformcloud2.executor.node.process;

import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.NetworkInfo;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.util.MemoryCalculator;
import systems.reformcloud.reformcloud2.executor.api.common.utility.PortUtil;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class LocalNodeProcessManager implements NodeProcessManager {

    private final Collection<ProcessInformation> information = Collections.synchronizedCollection(new ProcessCopyOnWriteArrayList());

    @Nullable
    @Override
    public ProcessInformation getLocalCloudProcess(@Nonnull String name) {
        return Streams.filterToReference(information, e -> e.getName().equals(name) && isLocal(e.getProcessUniqueID())).orNothing();
    }

    @Nullable
    @Override
    public ProcessInformation getLocalCloudProcess(@Nonnull UUID uuid) {
        return Streams.filterToReference(information, e -> e.getProcessUniqueID().equals(uuid) && isLocal(e.getProcessUniqueID())).orNothing();
    }

    @Nonnull
    @Override
    public synchronized ProcessInformation prepareLocalProcess(@Nonnull ProcessConfiguration configuration,
                                                               @Nonnull Template template, boolean start) {
        return this.prepareLocalProcess(this.constructInfo(
                configuration, template, NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode()
        ), start);
    }

    @Nonnull
    @Override
    public ProcessInformation prepareLocalProcess(@Nonnull ProcessInformation processInformation, boolean start) {
        this.handleProcessStart(processInformation);
        NodeInformation information = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode();
        information.addUsedMemory(processInformation.getTemplate().getRuntimeConfiguration().getMaxMemory());
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
    public ProcessInformation stopLocalProcess(@Nonnull String name) {
        List<RunningProcess> processes = LocalProcessManager.getNodeProcesses()
                .stream()
                .filter(e -> e.getProcessInformation().getName().equals(name))
                .collect(Collectors.toList());
        if (processes.isEmpty()) {
            return null;
        }

        processes.forEach(RunningProcess::shutdown);
        return processes.get(0).getProcessInformation();
    }

    @Nullable
    @Override
    public ProcessInformation stopLocalProcess(@Nonnull UUID uuid) {
        List<RunningProcess> processes = LocalProcessManager.getNodeProcesses()
                .stream()
                .filter(e -> e.getProcessInformation().getProcessUniqueID().equals(uuid))
                .collect(Collectors.toList());
        if (processes.isEmpty()) {
            return null;
        }

        processes.forEach(RunningProcess::shutdown);
        return processes.get(0).getProcessInformation();
    }

    @Nonnull
    @Override
    public synchronized ProcessInformation queueProcess(@Nonnull ProcessConfiguration configuration,
                                                        @Nonnull Template template, @Nonnull NodeInformation node,
                                                        boolean start) {
        ProcessInformation processInformation = constructInfo(configuration, template, node);
        this.handleProcessStart(processInformation);

        DefaultChannelManager.INSTANCE.get(node.getName()).ifPresent(e -> e.sendPacket(new PacketOutHeadNodeStartProcess(
                processInformation, start
        )));
        return processInformation;
    }

    @Override
    public void registerLocalProcess(@Nonnull RunningProcess process) {
        this.information.add(process.getProcessInformation());
    }

    @Override
    public void unregisterLocalProcess(@Nonnull UUID uniqueID) {
        Streams.filterToReference(information, e -> e.getProcessUniqueID().equals(uniqueID)).ifPresent(information::remove);
    }

    @Override
    public void handleLocalProcessStart(@Nonnull ProcessInformation processInformation) {
        handleProcessStart(processInformation);
        NodeInformation information = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode();
        information.getStartedProcesses().add(new NodeProcess(
                processInformation.getProcessGroup().getName(),
                processInformation.getName(),
                processInformation.getProcessUniqueID()
        ));
        NodeExecutor.getInstance().getClusterSyncManager().syncSelfInformation();
    }

    @Override
    public void handleLocalProcessStop(@Nonnull ProcessInformation processInformation) {
        handleProcessStop(processInformation);
        NodeInformation information = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode();
        information.removeUsedMemory(processInformation.getTemplate().getRuntimeConfiguration().getMaxMemory());
        Streams.filterToReference(information.getStartedProcesses(), e -> e.getUniqueID().equals(processInformation.getProcessUniqueID()))
                .ifPresent(information.getStartedProcesses()::remove);
        NodeExecutor.getInstance().getClusterSyncManager().syncSelfInformation();
    }

    @Override
    public void handleProcessStart(@Nonnull ProcessInformation processInformation) {
        ProcessInformation information = Streams.filterToReference(this.information,
                e -> e.getProcessUniqueID().equals(processInformation.getProcessUniqueID())).orNothing();
        if (information == null) {
            this.information.add(processInformation);
            return;
        }

        this.information.remove(information);  // Prevent double registrations
        this.information.add(processInformation);
    }

    @Override
    public void handleProcessUpdate(@Nonnull ProcessInformation processInformation) {
        Streams.filterToReference(this.information, e -> e.getProcessUniqueID().equals(processInformation.getProcessUniqueID())).ifPresent(e -> {
            this.information.remove(e);
            this.information.add(processInformation);
        });
    }

    @Override
    public void handleProcessConnection(@Nonnull ProcessInformation processInformation) {
        Task.EXECUTOR.execute(() -> {
            while (!DefaultChannelManager.INSTANCE.get(processInformation.getName()).isPresent()) {
                AbsoluteThread.sleep(5);
            }

            DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new ControllerEventProcessUpdated(processInformation)));
        });
    }

    @Override
    public void handleProcessStop(@Nonnull ProcessInformation processInformation) {
        Streams.filterToReference(information, e -> e.getProcessUniqueID().equals(processInformation.getProcessUniqueID())).ifPresent(information::remove);
    }

    @Override
    public void handleProcessDisconnect(@Nonnull String name) {
        ProcessInformation information = getLocalCloudProcess(name);
        if (information == null) {
            return;
        }

        this.information.remove(information);
        removeProcess(information);
    }

    @Override
    public boolean isLocal(@Nonnull String name) {
        return Streams.filterToReference(information, e -> e.getName().equals(name)
                && e.getNodeUniqueID().equals(NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getNodeUniqueID()))
                .isPresent();
    }

    @Override
    public boolean isLocal(@Nonnull UUID uniqueID) {
        return Streams.filterToReference(information, e -> e.getProcessUniqueID().equals(uniqueID)
                && e.getNodeUniqueID().equals(NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getNodeUniqueID()))
                .isPresent();
    }

    @Nonnull
    @Override
    public Collection<ProcessInformation> getClusterProcesses() {
        return information;
    }

    @Nonnull
    @Override
    public Collection<ProcessInformation> getClusterProcesses(@Nonnull String group) {
        return Streams.allOf(information, e -> e.getProcessGroup().getName().equals(group));
    }

    @Nonnull
    @Override
    public Collection<ProcessInformation> getLocalProcesses() {
        return Streams.allOf(getClusterProcesses(), e -> e.getNodeUniqueID().equals(
                NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getNodeUniqueID()
        ));
    }

    @Nullable
    @Override
    public ProcessInformation getClusterProcess(@Nonnull String name) {
        return Streams.filterToReference(information, e -> e.getName().equals(name)).orNothing();
    }

    @Nullable
    @Override
    public ProcessInformation getClusterProcess(@Nonnull UUID uniqueID) {
        return Streams.filterToReference(information, e -> e.getProcessUniqueID().equals(uniqueID)).orNothing();
    }

    @Nonnull
    @Override
    public Iterator<ProcessInformation> iterator() {
        return Streams.newList(information).iterator();
    }

    @Override
    public void update(@Nonnull ProcessInformation processInformation) {
        handleProcessUpdate(processInformation);
        NodeExecutor.getInstance().getClusterSyncManager().syncProcessUpdate(processInformation);
    }

    private void removeProcess(ProcessInformation information) {
        NodeProcess nodeProcess = Streams.filter(NodeExecutor.getInstance().getNodeNetworkManager()
                .getCluster().getSelfNode().getStartedProcesses(), e -> e.getUniqueID().equals(information.getProcessUniqueID()));
        if (nodeProcess == null) {
            return;
        }

        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getStartedProcesses().remove(nodeProcess);
        NodeExecutor.getInstance().getClusterSyncManager().syncProcessStop(information);
    }

    private int nextID(ProcessGroup processGroup) {
        int id = 1;
        Collection<Integer> ids = Streams.newCollection(information, processInformation -> processInformation.getProcessGroup().getName().equals(processGroup.getName()), ProcessInformation::getId);

        while (ids.contains(id)) {
            id++;
        }

        return id;
    }

    private int nextPort(int startPort) {
        Collection<Integer> ports = Streams.newList(information).stream().map(e -> e.getNetworkInfo().getPort()).collect(Collectors.toList());
        while (ports.contains(startPort)) {
            startPort++;
        }

        startPort = PortUtil.checkPort(startPort);
        return startPort;
    }

    private synchronized ProcessInformation constructInfo(ProcessConfiguration configuration, Template template, NodeInformation node) {
        int id = configuration.getId() == -1 ? this.nextID(configuration.getBase()) : configuration.getId();
        int port = configuration.getPort() == null ? nextPort(configuration.getBase().getStartupConfiguration().getStartPort())
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
            if (allProcess.getId() == id) {
                id = nextID(configuration.getBase());
            }

            if (allProcess.getNetworkInfo().getPort() == port) {
                port = nextPort(configuration.getBase().getStartupConfiguration().getStartPort());
            }

            if (allProcess.getProcessUniqueID().equals(uniqueID)) {
                uniqueID = UUID.randomUUID();
            }

            if (allProcess.getDisplayName().equals(displayName)) {
                displayName += UUID.randomUUID().toString().split("-")[0];
            }
        }

        ProcessInformation processInformation = new ProcessInformation(
                configuration.getBase().getName() + template.getServerNameSplitter() + id,
                displayName,
                node.getName(),
                node.getNodeUniqueID(),
                uniqueID,
                configuration.getMaxMemory() == null
                        ? MemoryCalculator.calcMemory(configuration.getBase().getName(), template)
                        : configuration.getMaxMemory(),
                id,
                ProcessState.CREATED,
                new NetworkInfo(
                        NodeExecutor.getInstance().getNodeConfig().getStartHost(),
                        port,
                        false
                ), configuration.getBase(),
                template,
                ProcessRuntimeInformation.empty(),
                new ArrayList<>(),
                configuration.getExtra(),
                configuration.getMaxPlayers(),
                configuration.getInclusions()
        );
        return processInformation.updateMaxPlayers(null);
    }
}