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

import java.util.UUID;

public class APIPacketOutLogoutPlayer implements Packet {

    public APIPacketOutLogoutPlayer(UUID playerUniqueID, String playerName, String lastServer) {
        this.playerUniqueID = playerUniqueID;
        this.playerName = playerName;
        this.lastServer = lastServer;
    }

    protected UUID playerUniqueID;

    protected String playerName;

    protected String lastServer;

    @Override
    public int getId() {
        return NetworkUtil.PLAYER_INFORMATION_BUS + 3;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.playerUniqueID);
        buffer.writeString(this.playerName);
        buffer.writeString(this.lastServer);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.playerUniqueID = buffer.readUniqueId();
        this.playerName = buffer.readString();
        this.lastServer = buffer.readString();
    }
}
