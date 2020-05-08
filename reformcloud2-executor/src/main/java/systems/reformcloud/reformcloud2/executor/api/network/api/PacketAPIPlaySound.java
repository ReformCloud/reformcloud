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

public class PacketAPIPlaySound extends Packet {

    public PacketAPIPlaySound() {
    }

    public PacketAPIPlaySound(UUID player, String sound, float f1, float f2) {
        this.player = player;
        this.sound = sound;
        this.f1 = f1;
        this.f2 = f2;
    }

    private UUID player;

    private String sound;

    private float f1;

    private float f2;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 204;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        APIConstants.playerAPIExecutor.executePlaySound(this.player, this.sound, this.f1, this.f2);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.player);
        buffer.writeString(this.sound);
        buffer.writeFloat(this.f1);
        buffer.writeFloat(this.f2);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.player = buffer.readUniqueId();
        this.sound = buffer.readString();
        this.f1 = buffer.readFloat();
        this.f2 = buffer.readFloat();
    }
}
