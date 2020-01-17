package systems.reformcloud.reformcloud2.executor.api.node.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

import java.util.Collection;
import java.util.function.Function;

public interface InternalNetworkCluster {

    ClusterManager getClusterManager();

    NodeInformation getHeadNode();

    NodeInformation getSelfNode();

    void updateSelf(NodeInformation self);

    default boolean isSelfNodeHead() {
        return getHeadNode() != null && getHeadNode().equals(getSelfNode());
    }

    NodeInformation getNode(String name);

    Collection<NodeInformation> getConnectedNodes();

    void handleNodeUpdate(NodeInformation nodeInformation);

    default boolean noOtherNodes() {
        return getConnectedNodes().isEmpty();
    }

    void publishToHeadNode(Packet packet);

    default <T> T sendQueryToHead(Packet query, Function<Packet, T> responseHandler) {
        if (getHeadNode().getNodeUniqueID().equals(getSelfNode().getNodeUniqueID())) {
            return null;
        }

        return sendQueryToNode(getHeadNode().getName(), query, responseHandler);
    }

    void broadCastToCluster(Packet packet);

    <T> T sendQueryToNode(String node, Packet query, Function<Packet, T> responseHandler);

    NodeInformation findBestNodeForStartup(ProcessGroup group, Template template);
}
