package systems.reformcloud.reformcloud2.executor.node.cluster.sync;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterSyncManager;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.SyncAction;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.event.ControllerEventProcessClosed;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.event.ControllerEventProcessStarted;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.event.ControllerEventProcessUpdated;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.client.NodeNetworkClient;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutNodeInformationUpdate;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster.*;
import systems.reformcloud.reformcloud2.executor.node.process.util.ProcessAction;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultClusterSyncManager implements ClusterSyncManager {

    public DefaultClusterSyncManager(NodeNetworkClient nodeNetworkClient) {
        this.nodeNetworkClient = nodeNetworkClient;
    }

    private final NodeNetworkClient nodeNetworkClient;

    private final Collection<String> waiting = new CopyOnWriteArrayList<>();

    private final Collection<ProcessGroup> processGroups = new CopyOnWriteArrayList<>();

    private final Collection<MainGroup> mainGroups = new CopyOnWriteArrayList<>();

    @Override
    public void syncSelfInformation() {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(
                new NodePacketOutNodeInformationUpdate(NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode())
        );
    }

    @Override
    public void syncProcessStartup(ProcessInformation processInformation) {
        NodeExecutor.getInstance().getNodeNetworkManager().getQueuedProcesses().remove(processInformation.getProcessUniqueID());
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutProcessAction(
                ProcessAction.START, processInformation
        ));
        sendToAllExcludedNodes(new ControllerEventProcessStarted(processInformation));
    }

    @Override
    public void syncProcessUpdate(ProcessInformation processInformation) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutProcessAction(
                ProcessAction.UPDATE, processInformation
        ));
        sendToAllExcludedNodes(new ControllerEventProcessUpdated(processInformation));
    }

    @Override
    public void syncProcessStop(ProcessInformation processInformation) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutProcessAction(
                ProcessAction.STOP, processInformation
        ));
        sendToAllExcludedNodes(new ControllerEventProcessClosed(processInformation));
    }

    @Override
    public void syncProcessGroups(Collection<ProcessGroup> processGroups, SyncAction action) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutSyncProcessGroups(processGroups, action));
    }

    @Override
    public void syncMainGroups(Collection<MainGroup> mainGroups, SyncAction action) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutSyncMainGroups(mainGroups, action));
    }

    @Override
    public void syncProcessInformation(Collection<ProcessInformation> information) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutSyncProcessInformation(information));
    }

    @Override
    public Collection<ProcessGroup> getProcessGroups() {
        return processGroups;
    }

    @Override
    public Collection<MainGroup> getMainGroups() {
        return mainGroups;
    }

    @Override
    public boolean existsProcessGroup(String name) {
        return Streams.filterToReference(this.processGroups, e -> e.getName().equals(name)).isPresent();
    }

    @Override
    public boolean existsMainGroup(String name) {
        return Streams.filterToReference(this.mainGroups, e -> e.getName().equals(name)).isPresent();
    }

    @Override
    public void syncProcessGroupCreate(ProcessGroup group) {
        this.processGroups.add(group);
        this.syncProcessGroups(this.processGroups, SyncAction.CREATE);

        NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupCreate(group);
        NodeExecutor.getInstance().getLocalAutoStartupHandler().update();
    }

    @Override
    public void syncMainGroupCreate(MainGroup group) {
        this.mainGroups.add(group);
        this.syncMainGroups(this.mainGroups, SyncAction.CREATE);

        NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupCreate(group);
    }

    @Override
    public void syncProcessGroupUpdate(ProcessGroup processGroup) {
        Streams.filterToReference(this.processGroups, e -> e.getName().equals(processGroup.getName())).ifPresent(e -> {
            this.processGroups.remove(e);
            this.processGroups.add(processGroup);
            this.syncProcessGroups(processGroups, SyncAction.UPDATE);

            NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupUpdate(processGroup);
            NodeExecutor.getInstance().getLocalAutoStartupHandler().update();
        });
    }

    @Override
    public void syncMainGroupUpdate(MainGroup mainGroup) {
        Streams.filterToReference(this.mainGroups, e -> e.getName().equals(mainGroup.getName())).ifPresent(e -> {
            this.mainGroups.remove(e);
            this.mainGroups.add(mainGroup);
            this.syncMainGroups(mainGroups, SyncAction.UPDATE);

            NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupUpdate(mainGroup);
        });
    }

    @Override
    public void syncProcessGroupDelete(String name) {
        Streams.filterToReference(this.processGroups, e -> e.getName().equals(name)).ifPresent(e -> {
            this.processGroups.remove(e);
            this.syncProcessGroups(processGroups, SyncAction.DELETE);

            NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupDelete(e);
            NodeExecutor.getInstance().getLocalAutoStartupHandler().update();
        });
    }

    @Override
    public void syncMainGroupDelete(String name) {
        Streams.filterToReference(this.mainGroups, e -> e.getName().equals(name)).ifPresent(e -> {
            this.mainGroups.remove(e);
            this.syncMainGroups(mainGroups, SyncAction.DELETE);

            NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupDelete(e);
        });
    }

    @Override
    public void handleProcessGroupSync(Collection<ProcessGroup> groups, SyncAction action) {
        switch (action) {
            case CREATE: {
                groups
                        .stream()
                        .filter(e -> this.processGroups.stream().noneMatch(g -> g.getName().equals(e.getName())))
                        .forEach(e -> {
                            this.processGroups.add(e);
                            NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupCreate(e);
                        });
                break;
            }

            case SYNC:
            case UPDATE: {
                groups.forEach(newGroup -> Streams.newList(this.processGroups).forEach(oldGroup -> {
                    if (newGroup.getName().equals(oldGroup.getName()) && !newGroup.equals(oldGroup)) {
                        this.processGroups.remove(oldGroup);
                        this.processGroups.add(newGroup);

                        NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupUpdate(newGroup);
                    }
                }));

                if (action.equals(SyncAction.SYNC)) {
                    processGroups.addAll(Streams.allOf(groups, g -> processGroups.stream().noneMatch(e -> e.getName().equals(g.getName()))));
                }
                break;
            }

            case DELETE: {
                Streams.allOf(processGroups, e -> groups.stream().noneMatch(g -> g.getName().equals(e.getName()))).forEach(e -> {
                    this.processGroups.remove(e);
                    NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupDelete(e);
                });
                break;
            }
        }

        NodeExecutor.getInstance().getLocalAutoStartupHandler().update();
    }

    @Override
    public void handleMainGroupSync(Collection<MainGroup> groups, SyncAction action) {
        switch (action) {
            case CREATE: {
                groups
                        .stream()
                        .filter(e -> this.mainGroups.stream().noneMatch(g -> g.getName().equals(e.getName())))
                        .forEach(e -> {
                            mainGroups.add(e);
                            NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupCreate(e);
                        });
                break;
            }

            case SYNC:
            case UPDATE: {
                groups.forEach(newGroup -> Streams.newList(this.mainGroups).forEach(oldGroup -> {
                    if (newGroup.getName().equals(oldGroup.getName()) && !newGroup.equals(oldGroup)) {
                        this.mainGroups.remove(oldGroup);
                        this.mainGroups.add(newGroup);

                        NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupUpdate(newGroup);
                    }
                }));

                if (action.equals(SyncAction.SYNC)) {
                    mainGroups.addAll(Streams.allOf(groups, g -> mainGroups.stream().noneMatch(e -> e.getName().equals(g.getName()))));
                }
                break;
            }

            case DELETE: {
                Streams.allOf(mainGroups, e -> groups.stream().noneMatch(g -> g.getName().equals(e.getName()))).forEach(e -> {
                    this.mainGroups.remove(e);
                    NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupDelete(e);
                });
                break;
            }
        }
    }

    @Override
    public void handleProcessInformationSync(Collection<ProcessInformation> information) {
        Collection<ProcessInformation> clusterProcesses = NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().getClusterProcesses();
        Streams.allOf(information, e -> clusterProcesses.stream().noneMatch(i -> i.getProcessUniqueID().equals(e.getProcessUniqueID())))
                .forEach(e -> {
                    clusterProcesses.add(e);
                    sendToAllExcludedNodes(new ControllerEventProcessStarted(e));
                });
    }

    @Override
    public void handleClusterReload() {
        try {
            NodeExecutor.getInstance().reload(false);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleNodeInformationUpdate(NodeInformation nodeInformation) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().handleNodeUpdate(nodeInformation);
    }

    @Override
    public void doClusterReload() {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutReloadCluster());
    }

    @Override
    public void disconnectFromCluster() {
        nodeNetworkClient.disconnect();
    }

    @Override
    public boolean isConnectedAndSyncWithCluster() {
        return waiting.isEmpty();
    }

    @Override
    public Collection<String> getWaitingConnections() {
        return waiting;
    }

    public static void sendToAllExcludedNodes(Packet packet) {
        Streams.allOf(DefaultChannelManager.INSTANCE.getAllSender(),
                e -> NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getNode(e.getName()) == null
        ).forEach(e -> e.sendPacket(packet));
    }
}
