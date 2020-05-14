package systems.reformcloud.reformcloud2.executor.node.process;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeProcess;
import systems.reformcloud.reformcloud2.executor.api.common.process.NetworkInfo;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.PortUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.executor.api.node.process.LocalNodeProcess;
import systems.reformcloud.reformcloud2.executor.api.node.process.NodeProcessManager;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.event.ControllerEventProcessUpdated;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.PacketOutHeadNodeStartProcess;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;
import systems.reformcloud.reformcloud2.executor.node.process.startup.LocalProcessQueue;
import systems.reformcloud.reformcloud2.executor.node.util.ProcessCopyOnWriteArrayList;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class LocalNodeProcessManager implements NodeProcessManager {

    private final Collection<ProcessInformation> information = Collections.synchronizedCollection(new ProcessCopyOnWriteArrayList());

    @Override
    public ProcessInformation getLocalCloudProcess(String name) {
        return Streams.filterToReference(information, e -> e.getName().equals(name) && isLocal(e.getProcessUniqueID())).orNothing();
    }

    @Override
    public ProcessInformation getLocalCloudProcess(UUID uuid) {
        return Streams.filterToReference(information, e -> e.getProcessUniqueID().equals(uuid) && isLocal(e.getProcessUniqueID())).orNothing();
    }

    @Override
    public ProcessInformation startLocalProcess(ProcessGroup processGroup, Template template, JsonConfiguration data, UUID processUniqueID) {
        int id = nextID(processGroup);
        ProcessInformation processInformation = new ProcessInformation(
                processGroup.getName() + template.getServerNameSplitter() + id,
                processGroup.getName() + (processGroup.isShowIdInName() ? (template.getServerNameSplitter() + id) : ""),
                NodeExecutor.getInstance().getNodeConfig().getName(),
                NodeExecutor.getInstance().getNodeConfig().getUniqueID(),
                processUniqueID,
                id,
                ProcessState.PREPARED,
                new NetworkInfo(
                        NodeExecutor.getInstance().getNodeConfig().getStartHost(),
                        nextPort(processGroup.getStartupConfiguration().getStartPort()),
                        false
                ), processGroup,
                template,
                ProcessRuntimeInformation.empty(),
                new ArrayList<>(),
                data,
                processGroup.getPlayerAccessConfiguration().getMaxPlayers()
        );

        return this.startLocalProcess(processInformation);
    }

    @Override
    public ProcessInformation startLocalProcess(ProcessInformation processInformation) {
        this.handleProcessStart(processInformation);
        NodeInformation information = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode();
        information.addUsedMemory(processInformation.getTemplate().getRuntimeConfiguration().getMaxMemory());
        NodeExecutor.getInstance().getClusterSyncManager().syncSelfInformation();
        LocalProcessQueue.queue(processInformation);
        return processInformation;
    }

    @Override
    public ProcessInformation stopLocalProcess(String name) {
        List<LocalNodeProcess> processes = LocalProcessManager.getNodeProcesses()
                .stream()
                .filter(e -> e.getProcessInformation().getName().equals(name))
                .collect(Collectors.toList());
        if (processes.isEmpty()) {
            return null;
        }

        processes.forEach(LocalNodeProcess::shutdown);
        return processes.get(0).getProcessInformation();
    }

    @Override
    public ProcessInformation stopLocalProcess(UUID uuid) {
        List<LocalNodeProcess> processes = LocalProcessManager.getNodeProcesses()
                .stream()
                .filter(e -> e.getProcessInformation().getProcessUniqueID().equals(uuid))
                .collect(Collectors.toList());
        if (processes.isEmpty()) {
            return null;
        }

        processes.forEach(LocalNodeProcess::shutdown);
        return processes.get(0).getProcessInformation();
    }

    @Override
    public ProcessInformation queueProcess(ProcessGroup processGroup, Template template, JsonConfiguration data, NodeInformation node, UUID uniqueID) {
        ProcessInformation processInformation = constructCaInfo(processGroup, template, data, node, uniqueID);
        this.handleProcessStart(processInformation);

        DefaultChannelManager.INSTANCE.get(node.getName()).ifPresent(e -> e.sendPacket(new PacketOutHeadNodeStartProcess(
                processInformation
        )));
        return processInformation;
    }

    @Override
    public void registerLocalProcess(LocalNodeProcess process) {
        this.information.add(process.getProcessInformation());
    }

    @Override
    public void unregisterLocalProcess(UUID uniqueID) {
        Streams.filterToReference(information, e -> e.getProcessUniqueID().equals(uniqueID)).ifPresent(information::remove);
    }

    @Override
    public void handleLocalProcessStart(ProcessInformation processInformation) {
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
    public void handleLocalProcessStop(ProcessInformation processInformation) {
        handleProcessStop(processInformation);
        NodeInformation information = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode();
        information.removeUsedMemory(processInformation.getTemplate().getRuntimeConfiguration().getMaxMemory());
        Streams.filterToReference(information.getStartedProcesses(), e -> e.getUniqueID().equals(processInformation.getProcessUniqueID()))
                .ifPresent(information.getStartedProcesses()::remove);
        NodeExecutor.getInstance().getClusterSyncManager().syncSelfInformation();
    }

    @Override
    public void handleProcessStart(ProcessInformation processInformation) {
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
    public void handleProcessUpdate(ProcessInformation processInformation) {
        Streams.filterToReference(this.information, e -> e.getProcessUniqueID().equals(processInformation.getProcessUniqueID())).ifPresent(e -> {
            this.information.remove(e);
            this.information.add(processInformation);
        });
    }

    @Override
    public void handleProcessConnection(ProcessInformation processInformation) {
        Task.EXECUTOR.execute(() -> {
            while (!DefaultChannelManager.INSTANCE.get(processInformation.getName()).isPresent()) {
                AbsoluteThread.sleep(5);
            }

            DefaultChannelManager.INSTANCE.get(processInformation.getName()).ifPresent(e -> e.sendPacket(new ControllerEventProcessUpdated(processInformation)));
        });
    }

    @Override
    public void handleProcessStop(ProcessInformation processInformation) {
        Streams.filterToReference(information, e -> e.getProcessUniqueID().equals(processInformation.getProcessUniqueID())).ifPresent(information::remove);
    }

    @Override
    public void handleProcessDisconnect(String name) {
        ProcessInformation information = getLocalCloudProcess(name);
        if (information == null) {
            return;
        }

        this.information.remove(information);
        removeProcess(information);
    }

    @Override
    public boolean isLocal(String name) {
        return Streams.filterToReference(information, e -> e.getName().equals(name)
                && e.getNodeUniqueID().equals(NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getNodeUniqueID()))
                .isPresent();
    }

    @Override
    public boolean isLocal(UUID uniqueID) {
        return Streams.filterToReference(information, e -> e.getProcessUniqueID().equals(uniqueID)
                && e.getNodeUniqueID().equals(NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getNodeUniqueID()))
                .isPresent();
    }

    @Override
    public Collection<ProcessInformation> getClusterProcesses() {
        return information;
    }

    @Override
    public Collection<ProcessInformation> getClusterProcesses(String group) {
        return Streams.allOf(information, e -> e.getProcessGroup().getName().equals(group));
    }

    @Override
    public Collection<ProcessInformation> getLocalProcesses() {
        return Streams.allOf(getClusterProcesses(), e -> e.getNodeUniqueID().equals(
                NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getNodeUniqueID()
        ));
    }

    @Override
    public ProcessInformation getClusterProcess(String name) {
        return Streams.filterToReference(information, e -> e.getName().equals(name)).orNothing();
    }

    @Override
    public ProcessInformation getClusterProcess(UUID uniqueID) {
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
        synchronized (information) {
            int id = 1;
            Collection<Integer> ids = Streams.newCollection(information, processInformation -> processInformation.getProcessGroup().getName().equals(processGroup.getName()), ProcessInformation::getId);

            while (ids.contains(id)) {
                id++;
            }

            return id;
        }
    }

    private int nextPort(int startPort) {
        synchronized (information) {
            Collection<Integer> ports = Streams.newList(information).stream().map(e -> e.getNetworkInfo().getPort()).collect(Collectors.toList());
            while (ports.contains(startPort)) {
                startPort++;
            }

            startPort = PortUtil.checkPort(startPort);
            return startPort;
        }
    }

    private ProcessInformation constructCaInfo(ProcessGroup processGroup, Template template, JsonConfiguration data,
                                               NodeInformation node, UUID uniqueID) {
        int id = nextID(processGroup);
        return new ProcessInformation(
                processGroup.getName() + template.getServerNameSplitter() + id,
                processGroup.getName() + (processGroup.isShowIdInName() ? (template.getServerNameSplitter() + id) : ""),
                node.getName(),
                node.getNodeUniqueID(),
                uniqueID,
                id,
                ProcessState.PREPARED,
                new NetworkInfo(
                        null,
                        -1,
                        false
                ), processGroup,
                template,
                ProcessRuntimeInformation.empty(),
                new ArrayList<>(),
                data,
                processGroup.getPlayerAccessConfiguration().getMaxPlayers()
        );
    }
}