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

public class APIBungeePacketOutPlayerServerSwitch implements Packet {

    public APIBungeePacketOutPlayerServerSwitch() {
    }

    public APIBungeePacketOutPlayerServerSwitch(UUID playerUniqueID, String originalServer, String targetServer) {
        this.playerUniqueID = playerUniqueID;
        this.originalServer = originalServer;
        this.targetServer = targetServer;
    }

    protected UUID playerUniqueID;

    protected String playerName;

    protected String originalServer;

    protected String targetServer;

    @Override
    public int getId() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 12;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.playerUniqueID);
        buffer.writeString(this.playerName);
        buffer.writeString(this.originalServer);
        buffer.writeString(this.targetServer);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.playerUniqueID = buffer.readUniqueId();
        this.playerName = buffer.readString();
        this.originalServer = buffer.readString();
        this.targetServer = buffer.readString();
    }
}
