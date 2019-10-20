package systems.reformcloud.reformcloud2.executor.node.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

public class DefaultClusterManager implements ClusterManager {

    private final Collection<NodeInformation> nodeInformation = new ArrayList<>();

    private NodeInformation head;

    @Override
    public void init() {
        nodeInformation.add(NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode());
    }

    @Override
    public void handleNodeDisconnect(InternalNetworkCluster cluster, String name) {
        Links.allOf(Links.newList(nodeInformation), e -> e.getName().equals(name)).forEach(e -> {
            this.nodeInformation.remove(e);
            cluster.getConnectedNodes().remove(e);
            Links.allOf(Links.newList(NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().getClusterProcesses()),
                    i -> i.getNodeUniqueID().equals(e.getNodeUniqueID())
            ).forEach(NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().getClusterProcesses()::remove);

            if (head != null && head.getNodeUniqueID().equals(e.getNodeUniqueID())) {
                head = null;
            }
        });

        recalculateHead();
    }

    @Override
    public void handleConnect(InternalNetworkCluster cluster, NodeInformation nodeInformation, BiConsumer<Boolean, String> result) {
        if (this.nodeInformation.stream().anyMatch(e -> e.getName().equals(nodeInformation.getName()))) {
            result.accept(false, "A node with this name is already connected");
            return;
        }

        this.nodeInformation.add(nodeInformation);
        cluster.getConnectedNodes().add(nodeInformation);
        recalculateHead();
        result.accept(true, null);
    }

    @Override
    public int getOnlineAndWaiting(String groupName) {
        int onlineOrWaiting = Links.allOf(NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().getClusterProcesses(),
                e -> e.getProcessGroup().getName().equals(groupName)).size();
        onlineOrWaiting += Links.deepFilter(NodeExecutor.getInstance().getNodeNetworkManager().getQueuedProcesses(),
                v -> v.getValue().equals(groupName)).size();
        return onlineOrWaiting;
    }

    @Override
    public NodeInformation getHeadNode() {
        if (head == null) {
            recalculateHead();
        }

        return head;
    }

    private void recalculateHead() {
        for (NodeInformation information : nodeInformation) {
            if (head == null) {
                head = information;
                continue;
            }

            if (information.getStartupTime() < head.getStartupTime()) {
                head = information;
            }
        }
    }
}
