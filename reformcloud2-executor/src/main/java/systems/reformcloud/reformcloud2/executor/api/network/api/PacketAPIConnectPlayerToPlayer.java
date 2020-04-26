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

public class PacketAPIConnectPlayerToPlayer implements Packet {

    public PacketAPIConnectPlayerToPlayer() {
    }

    public PacketAPIConnectPlayerToPlayer(UUID playerToSent, UUID targetPlayer) {
        this.playerToSent = playerToSent;
        this.targetPlayer = targetPlayer;
    }

    private UUID playerToSent;

    private UUID targetPlayer;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 200;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        APIConstants.playerAPIExecutor.executeConnect(this.playerToSent, this.targetPlayer);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.playerToSent);
        buffer.writeUniqueId(this.targetPlayer);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.playerToSent = buffer.readUniqueId();
        this.targetPlayer = buffer.readUniqueId();
    }
}
