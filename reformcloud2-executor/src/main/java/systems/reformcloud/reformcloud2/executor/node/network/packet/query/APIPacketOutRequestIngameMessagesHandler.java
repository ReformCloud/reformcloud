package systems.reformcloud.reformcloud2.executor.node.network.packet.query;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutRequestIngameMessages;
import systems.reformcloud.reformcloud2.executor.api.network.packets.out.APIPacketOutRequestIngameMessagesResult;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

public class APIPacketOutRequestIngameMessagesHandler extends APIPacketOutRequestIngameMessages {

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        sender.sendQueryResult(
                this.getQueryUniqueID(),
                new APIPacketOutRequestIngameMessagesResult(NodeExecutor.getInstance().getNodeExecutorConfig().getIngameMessages())
        );
    }
}
