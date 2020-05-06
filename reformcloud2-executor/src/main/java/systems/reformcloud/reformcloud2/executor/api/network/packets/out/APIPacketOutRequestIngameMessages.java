package systems.reformcloud.reformcloud2.executor.api.network.packets.out;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public class APIPacketOutRequestIngameMessages extends Packet {

    public APIPacketOutRequestIngameMessages() {
    }

    @Override
    public int getId() {
        return NetworkUtil.CONTROLLER_QUERY_BUS + 2;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
    }
}
