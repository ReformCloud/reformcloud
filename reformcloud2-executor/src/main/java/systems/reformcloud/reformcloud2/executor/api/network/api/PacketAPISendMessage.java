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

public class PacketAPISendMessage extends Packet {

    public PacketAPISendMessage() {
    }

    public PacketAPISendMessage(UUID targetPlayer, String message) {
        this.targetPlayer = targetPlayer;
        this.message = message;
    }

    private UUID targetPlayer;

    private String message;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 205;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        APIConstants.playerAPIExecutor.executeSendMessage(this.targetPlayer, this.message);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.targetPlayer);
        buffer.writeString(this.message);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.targetPlayer = buffer.readUniqueId();
        this.message = buffer.readString();
    }
}
