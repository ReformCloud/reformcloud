package systems.reformcloud.reformcloud2.executor.node.network.channel;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.NodePacketOutConnectionInitDone;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;

public final class NodeNetworkSuccessHandler implements BiConsumer<ChannelHandlerContext, Packet> {

    @Override
    public void accept(ChannelHandlerContext channelHandlerContext, Packet packet) {
        ProcessInformation process = NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().getLocalCloudProcess(packet.content().getString("name"));

        JsonConfiguration result = new JsonConfiguration().add("access", true);
        if (process == null) {
            // Node
            result.add("name", NodeExecutor.getInstance().getNodeExecutorConfig().getSelf().getName());
        } else {
            result.add("name", "Controller");
        }

        channelHandlerContext.channel().writeAndFlush(new JsonPacket(-511, result)).syncUninterruptibly();

        if (process == null) {
            String address = ((InetSocketAddress) channelHandlerContext.channel().remoteAddress()).getAddress().getHostAddress();
            if (NodeExecutor.getInstance().getNodeConfig().getClusterNodes().stream().noneMatch(e -> e.getHost().equals(address))) {
                System.out.println(LanguageManager.get("network-node-connection-from-unknown-node", address));
                channelHandlerContext.channel().close().syncUninterruptibly();
                return;
            }

            NodeInformation nodeInformation = packet.content().get("info", NodeInformation.TYPE);
            if (nodeInformation == null) {
                System.out.println(LanguageManager.get("network-node-connection-from-unknown-node", address));
                channelHandlerContext.channel().close().syncUninterruptibly();
                return;
            }

            InternalNetworkCluster cluster = NodeExecutor.getInstance().getNodeNetworkManager().getCluster();
            if (cluster.getNode(nodeInformation.getName()) != null || cluster.getNode(nodeInformation.getNodeUniqueID()) != null) {
                System.out.println(LanguageManager.get("network-node-already-connected", nodeInformation.getName(), nodeInformation.getNodeUniqueID()));
                channelHandlerContext.channel().close().syncUninterruptibly();
                return;
            }

            NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getClusterManager().handleConnect(
                    NodeExecutor.getInstance().getNodeNetworkManager().getCluster(),
                    nodeInformation
            );

            channelHandlerContext.channel().writeAndFlush(new NodePacketOutConnectionInitDone(
                    NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode()
            )).syncUninterruptibly();
            NodeExecutor.getInstance().getClusterSyncManager().getWaitingConnections().remove(address);
            NodeExecutor.getInstance().sync(packet.content().getString("name"));

            System.out.println(LanguageManager.get("network-node-other-node-connected", nodeInformation.getName(), address));
        } else {
            process.getNetworkInfo().setConnected(true);
            process.getProcessDetail().setProcessState(ProcessState.READY);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(process);
            NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessConnection(process);

            System.out.println(LanguageManager.get("process-connected", process.getProcessDetail().getName(), process.getProcessDetail().getParentName()));
        }
    }
}
