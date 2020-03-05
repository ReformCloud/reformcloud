package systems.reformcloud.reformcloud2.executor.node.network.channel;

import io.netty.channel.ChannelHandlerContext;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.shared.ClientChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.client.NodeNetworkClient;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;

public class NodeNetworkSuccessHandler implements BiConsumer<ChannelHandlerContext, Packet> {

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
            Streams.filterToReference(NodeExecutor.getInstance().getNodeConfig().getOtherNodes(), e -> e.keySet().stream().anyMatch(c -> c.equals(
                    address
            ))).ifPresent(e -> e.forEach((key, value) -> NodeExecutor.getInstance().getNetworkClient().connect(
                    key,
                    value,
                    () -> new NodeNetworkChannelReader(NodeExecutor.getInstance().getPacketHandler()),
                    new ClientChallengeAuthHandler(
                            NodeExecutor.getInstance().getNodeExecutorConfig().getConnectionKey(),
                            NodeExecutor.getInstance().getNodeConfig().getName(),
                            () -> new JsonConfiguration().add("info", NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode()),
                            context -> NodeNetworkClient.CONNECTIONS.remove(address)
                    )
            )));
            NodeExecutor.getInstance().sync(packet.content().getString("name"));
        } else {
            process.getNetworkInfo().setConnected(true);
            process.setProcessState(ProcessState.READY);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(process);

            System.out.println(LanguageManager.get("process-connected", process.getName(), process.getParent()));
        }
    }
}
