package systems.reformcloud.reformcloud2.executor.node.cluster.sync;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterSyncManager;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.SyncAction;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventProcessClosed;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventProcessStarted;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventProcessUpdated;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.client.NodeNetworkClient;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutNodeInformationUpdate;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster.*;
import systems.reformcloud.reformcloud2.executor.node.process.util.ProcessAction;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DefaultClusterSyncManager implements ClusterSyncManager {

    public DefaultClusterSyncManager(NodeNetworkClient nodeNetworkClient) {
        this.nodeNetworkClient = nodeNetworkClient;
    }

    private final NodeNetworkClient nodeNetworkClient;

    private final Collection<String> waiting = new CopyOnWriteArrayList<>();

    private final Collection<ProcessGroup> processGroups = new CopyOnWriteArrayList<>();

    private final Collection<MainGroup> mainGroups = new CopyOnWriteArrayList<>();

    @Override
    public void syncSelfInformation() {
        NodeInformation self = NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode();
        self.update();

        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(
                new NodePacketOutNodeInformationUpdate(self)
        );
    }

    @Override
    public void syncProcessStartup(@NotNull ProcessInformation processInformation) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutProcessAction(
                ProcessAction.START, processInformation
        ));

        NodeExecutor.getInstance().getEventManager().callEvent(new ProcessStartedEvent(processInformation));
        sendToAllExcludedNodes(new ControllerEventProcessStarted(processInformation));
    }

    @Override
    public void syncProcessUpdate(@NotNull ProcessInformation processInformation) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutProcessAction(
                ProcessAction.UPDATE, processInformation
        ));

        NodeExecutor.getInstance().getEventManager().callEvent(new ProcessUpdatedEvent(processInformation));
        sendToAllExcludedNodes(new ControllerEventProcessUpdated(processInformation));
    }

    @Override
    public void syncProcessStop(@NotNull ProcessInformation processInformation) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutProcessAction(
                ProcessAction.STOP, processInformation
        ));

        NodeExecutor.getInstance().getEventManager().callEvent(new ProcessStoppedEvent(processInformation));
        sendToAllExcludedNodes(new ControllerEventProcessClosed(processInformation));
    }

    @Override
    public void syncProcessGroups(@NotNull Collection<ProcessGroup> processGroups, @NotNull SyncAction action) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutSyncProcessGroups(processGroups, action));
    }

    @Override
    public void syncMainGroups(Collection<MainGroup> mainGroups, SyncAction action) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutSyncMainGroups(mainGroups, action));
    }

    @Override
    public void syncProcessInformation(@NotNull Collection<ProcessInformation> information) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutSyncProcessInformation(information));
    }

    @NotNull
    @Override
    public Collection<ProcessGroup> getProcessGroups() {
        return processGroups;
    }

    @NotNull
    @Override
    public Collection<MainGroup> getMainGroups() {
        return mainGroups;
    }

    @Override
    public boolean existsProcessGroup(@NotNull String name) {
        return Streams.filterToReference(this.processGroups, e -> e.getName().equals(name)).isPresent();
    }

    @Override
    public boolean existsMainGroup(@NotNull String name) {
        return Streams.filterToReference(this.mainGroups, e -> e.getName().equals(name)).isPresent();
    }

    @Override
    public void syncProcessGroupCreate(@NotNull ProcessGroup group) {
        this.processGroups.add(group);
        this.syncProcessGroups(this.processGroups, SyncAction.CREATE);

        NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupCreate(group);
        NodeExecutor.getInstance().getLocalAutoStartupHandler().update();
    }

    @Override
    public void syncMainGroupCreate(@NotNull MainGroup group) {
        this.mainGroups.add(group);
        this.syncMainGroups(this.mainGroups, SyncAction.CREATE);

        NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupCreate(group);
    }

    @Override
    public void syncProcessGroupUpdate(@NotNull ProcessGroup processGroup) {
        Streams.filterToReference(this.processGroups, e -> e.getName().equals(processGroup.getName())).ifPresent(e -> {
            this.processGroups.remove(e);
            this.processGroups.add(processGroup);
            this.syncProcessGroups(processGroups, SyncAction.UPDATE);

            NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupUpdate(processGroup);
            NodeExecutor.getInstance().getLocalAutoStartupHandler().update();
        });
    }

    @Override
    public void syncMainGroupUpdate(@NotNull MainGroup mainGroup) {
        Streams.filterToReference(this.mainGroups, e -> e.getName().equals(mainGroup.getName())).ifPresent(e -> {
            this.mainGroups.remove(e);
            this.mainGroups.add(mainGroup);
            this.syncMainGroups(mainGroups, SyncAction.UPDATE);

            NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupUpdate(mainGroup);
        });
    }

    @Override
    public void syncProcessGroupDelete(@NotNull String name) {
        Streams.filterToReference(this.processGroups, e -> e.getName().equals(name)).ifPresent(e -> {
            this.processGroups.remove(e);
            this.syncProcessGroups(processGroups, SyncAction.DELETE);

            NodeExecutor.getInstance().getLocalAutoStartupHandler().update();
            NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupDelete(e);
        });
    }

    @Override
    public void syncMainGroupDelete(@NotNull String name) {
        Streams.filterToReference(this.mainGroups, e -> e.getName().equals(name)).ifPresent(e -> {
            this.mainGroups.remove(e);
            this.syncMainGroups(mainGroups, SyncAction.DELETE);

            NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupDelete(e);
        });
    }

    @Override
    public void handleProcessGroupSync(@NotNull Collection<ProcessGroup> groups, @NotNull SyncAction action) {
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
                    Collection<ProcessGroup> processGroups = Streams.allOf(groups, g -> this.processGroups.stream().noneMatch(e -> e.getName().equals(g.getName())));
                    this.processGroups.addAll(processGroups);
                    processGroups.forEach(e -> NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupCreate(e));
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
    public void handleMainGroupSync(@NotNull Collection<MainGroup> groups, @NotNull SyncAction action) {
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
                    Collection<MainGroup> mainGroups = Streams.allOf(groups, g -> this.mainGroups.stream().noneMatch(e -> e.getName().equals(g.getName())));
                    this.mainGroups.addAll(mainGroups);
                    mainGroups.forEach(e -> NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupCreate(e));
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
    public void handleProcessInformationSync(@NotNull Collection<ProcessInformation> information) {
        Collection<ProcessInformation> clusterProcesses = NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().getClusterProcesses();
        Streams.allOf(information, e -> clusterProcesses.stream().noneMatch(i -> i.getProcessDetail().getProcessUniqueID().equals(e.getProcessDetail().getProcessUniqueID())))
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
    public void handleNodeInformationUpdate(@NotNull NodeInformation nodeInformation) {
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

    @NotNull
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
