package systems.reformcloud.reformcloud2.executor.api.network.api;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.APIConstants;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;

public class PacketAPITeleportPlayer extends Packet {

    public PacketAPITeleportPlayer() {
    }

    public PacketAPITeleportPlayer(UUID targetPlayer, String world, double x, double y, double z, float yaw, float pitch) {
        this.targetPlayer = targetPlayer;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    private UUID targetPlayer;

    private String world;

    private double x;

    private double y;

    private double z;

    private float yaw;

    private float pitch;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 207;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        APIConstants.playerAPIExecutor.executeTeleport(this.targetPlayer, this.world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.targetPlayer);
        buffer.writeString(this.world);
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
        buffer.writeFloat(this.yaw);
        buffer.writeFloat(this.pitch);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.targetPlayer = buffer.readUniqueId();
        this.world = buffer.readString();
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
        this.yaw = buffer.readFloat();
        this.pitch = buffer.readFloat();
    }
}
