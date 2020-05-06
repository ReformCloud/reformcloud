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

public class APIPacketOutPlayerLoggedIn extends Packet {

    public APIPacketOutPlayerLoggedIn(String playerName) {
        this.playerName = playerName;
    }

    protected String playerName;

    @Override
    public int getId() {
        return NetworkUtil.PLAYER_INFORMATION_BUS + 2;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {

    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.playerName);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.playerName = buffer.readString();
    }
}
