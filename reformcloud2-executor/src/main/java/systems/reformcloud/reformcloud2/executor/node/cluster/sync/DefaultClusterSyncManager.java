package systems.reformcloud.reformcloud2.executor.node.cluster.sync;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterSyncManager;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.SyncAction;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.client.NodeNetworkClient;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutNodeInformationUpdate;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster.PacketOutProcessAction;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster.PacketOutReloadCluster;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster.PacketOutSyncMainGroups;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster.PacketOutSyncProcessGroups;
import systems.reformcloud.reformcloud2.executor.node.process.util.ProcessAction;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultClusterSyncManager implements ClusterSyncManager {

    public DefaultClusterSyncManager(NodeNetworkClient nodeNetworkClient) {
        this.nodeNetworkClient = nodeNetworkClient;
    }

    private final NodeNetworkClient nodeNetworkClient;

    private final Collection<String> waiting = new ArrayList<>();

    private final Collection<ProcessGroup> processGroups = new ArrayList<>();

    private final Collection<MainGroup> mainGroups = new ArrayList<>();

    @Override
    public void syncSelfInformation() {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(
                new NodePacketOutNodeInformationUpdate(NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode())
        );
    }

    @Override
    public void syncProcessStartup(ProcessInformation processInformation) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutProcessAction(
                ProcessAction.START, processInformation
        ));
    }

    @Override
    public void syncProcessUpdate(ProcessInformation processInformation) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutProcessAction(
                ProcessAction.UPDATE, processInformation
        ));
    }

    @Override
    public void syncProcessStop(ProcessInformation processInformation) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutProcessAction(
                ProcessAction.STOP, processInformation
        ));
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
    public Collection<ProcessGroup> getProcessGroups() {
        return processGroups;
    }

    @Override
    public Collection<MainGroup> getMainGroups() {
        return mainGroups;
    }

    @Override
    public boolean existsProcessGroup(String name) {
        return Links.filterToReference(this.processGroups, e -> e.getName().equals(name)).isPresent();
    }

    @Override
    public boolean existsMainGroup(String name) {
        return Links.filterToReference(this.mainGroups, e -> e.getName().equals(name)).isPresent();
    }

    @Override
    public void syncProcessGroupCreate(ProcessGroup group) {
        this.processGroups.add(group);
        this.syncProcessGroups(this.processGroups, SyncAction.CREATE);

        NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupCreate(group);
    }

    @Override
    public void syncMainGroupCreate(MainGroup group) {
        this.mainGroups.add(group);
        this.syncMainGroups(this.mainGroups, SyncAction.CREATE);

        NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupCreate(group);
    }

    @Override
    public void syncProcessGroupUpdate(ProcessGroup processGroup) {
        Links.filterToReference(this.processGroups, e -> e.getName().equals(processGroup.getName())).ifPresent(e -> {
            this.processGroups.remove(e);
            this.processGroups.add(processGroup);
            this.syncProcessGroups(processGroups, SyncAction.UPDATE);

            NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupUpdate(processGroup);
        });
    }

    @Override
    public void syncMainGroupUpdate(MainGroup mainGroup) {
        Links.filterToReference(this.mainGroups, e -> e.getName().equals(mainGroup.getName())).ifPresent(e -> {
            this.mainGroups.remove(e);
            this.mainGroups.add(mainGroup);
            this.syncMainGroups(mainGroups, SyncAction.UPDATE);

            NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupUpdate(mainGroup);
        });
    }

    @Override
    public void syncProcessGroupDelete(String name) {
        Links.filterToReference(this.processGroups, e -> e.getName().equals(name)).ifPresent(e -> {
            this.processGroups.remove(e);
            this.syncProcessGroups(processGroups, SyncAction.DELETE);

            NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupDelete(e);
        });
    }

    @Override
    public void syncMainGroupDelete(String name) {
        Links.filterToReference(this.mainGroups, e -> e.getName().equals(name)).ifPresent(e -> {
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
                groups.forEach(newGroup -> this.processGroups.forEach(oldGroup -> {
                    if (newGroup.getName().equals(oldGroup.getName()) && !newGroup.equals(oldGroup)) {
                        this.processGroups.remove(oldGroup);
                        this.processGroups.add(newGroup);

                        NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupUpdate(newGroup);
                    }
                }));

                if (action.equals(SyncAction.SYNC)) {
                    processGroups.addAll(Links.allOf(groups, g -> processGroups.stream().noneMatch(e -> e.getName().equals(g.getName()))));
                }
                break;
            }

            case DELETE: {
                Links.allOf(processGroups, e -> groups.stream().noneMatch(g -> g.getName().equals(e.getName()))).forEach(e -> {
                    this.processGroups.remove(e);
                    NodeExecutor.getInstance().getNodeExecutorConfig().handleProcessGroupDelete(e);
                });
                break;
            }
        }
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
                groups.forEach(newGroup -> this.mainGroups.forEach(oldGroup -> {
                    if (newGroup.getName().equals(oldGroup.getName()) && !newGroup.equals(oldGroup)) {
                        this.mainGroups.remove(oldGroup);
                        this.mainGroups.add(newGroup);

                        NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupUpdate(newGroup);
                    }
                }));

                if (action.equals(SyncAction.SYNC)) {
                    mainGroups.addAll(Links.allOf(groups, g -> mainGroups.stream().noneMatch(e -> e.getName().equals(g.getName()))));
                }
                break;
            }

            case DELETE: {
                Links.allOf(mainGroups, e -> groups.stream().noneMatch(g -> g.getName().equals(e.getName()))).forEach(e -> {
                    this.mainGroups.remove(e);
                    NodeExecutor.getInstance().getNodeExecutorConfig().handleMainGroupDelete(e);
                });
                break;
            }
        }
    }

    @Override
    public void handleClusterReload() {
        try {
            NodeExecutor.getInstance().reload();
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
        handleClusterReload();
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
}
