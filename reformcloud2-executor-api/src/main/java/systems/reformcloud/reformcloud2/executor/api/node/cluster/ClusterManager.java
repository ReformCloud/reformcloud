package systems.reformcloud.reformcloud2.executor.api.node.cluster;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

import java.util.function.BiConsumer;

public interface ClusterManager {

    void handleNodeDisconnect(InternalNetworkCluster cluster, String name);

    void handleConnect(InternalNetworkCluster cluster, PacketSender node, NodeInformation nodeInformation, BiConsumer<Boolean, String> result);

    NodeInformation getHeadNode();
}
