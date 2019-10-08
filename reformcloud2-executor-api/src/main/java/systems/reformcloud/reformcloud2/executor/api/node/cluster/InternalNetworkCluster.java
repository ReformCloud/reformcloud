package systems.reformcloud.reformcloud2.executor.api.node.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.Template;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

import java.util.Collection;
import java.util.function.Function;

public interface InternalNetworkCluster {

    NodeInformation getHeadNode();

    NodeInformation getSelfNode();

    void updateSelf(NodeInformation self);

    default boolean isSelfNodeHead() {
        return getHeadNode().equals(getSelfNode());
    }

    NodeInformation getNode(String name);

    Collection<NodeInformation> getConnectedNodes();

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

    NodeInformation findBestNodeForStartup(Template template);
}
