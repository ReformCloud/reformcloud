package systems.reformcloud.reformcloud2.executor.api.node.cluster;

import java.util.function.BiConsumer;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

public interface ClusterManager {

  void init();

  void handleNodeDisconnect(InternalNetworkCluster cluster, String name);

  void handleConnect(InternalNetworkCluster cluster,
                     NodeInformation nodeInformation,
                     BiConsumer<Boolean, String> result);

  int getOnlineAndWaiting(String groupName);

  NodeInformation getHeadNode();
}
