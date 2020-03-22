package systems.reformcloud.reformcloud2.executor.api.node.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

public interface ClusterManager {

    void init();

    void handleNodeDisconnect(InternalNetworkCluster cluster, String name);

    void handleConnect(InternalNetworkCluster cluster, NodeInformation nodeInformation);

    int getOnlineAndWaiting(String groupName);

    int getWaiting(String groupName);

    NodeInformation getHeadNode();
}
