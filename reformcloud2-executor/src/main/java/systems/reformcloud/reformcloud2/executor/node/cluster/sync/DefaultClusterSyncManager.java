package systems.reformcloud.reformcloud2.executor.node.cluster.sync;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterSyncManager;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.client.NodeNetworkClient;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster.PacketOutProcessAction;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster.PacketOutReloadCluster;
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
    public void syncProcessGroups(Collection<ProcessGroup> processGroups) {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutSyncProcessGroups(processGroups));
    }

    @Override
    public void syncMainGroup(Collection<MainGroup> mainGroups) {

    }

    @Override
    public Collection<ProcessGroup> getProcessGroups() {
        return processGroups;
    }

    @Override
    public Collection<MainGroup> getMainGroups() {
        return null;
    }

    @Override
    public boolean existsProcessGroup(String name) {
        return false;
    }

    @Override
    public boolean existsMainGroup(String name) {
        return false;
    }

    @Override
    public void syncProcessGroupCreate(ProcessGroup group) {

    }

    @Override
    public void syncMainGroupCreate(MainGroup group) {

    }

    @Override
    public void syncProcessGroupUpdate(ProcessGroup processGroup) {

    }

    @Override
    public void syncMainGroupUpdate(MainGroup mainGroup) {

    }

    @Override
    public void syncProcessGroupDelete(String name) {

    }

    @Override
    public void syncMainGroupDelete(String name) {

    }

    @Override
    public void doClusterReload() {
        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().broadCastToCluster(new PacketOutReloadCluster());

        try {
            NodeExecutor.getInstance().reload();
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }
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
