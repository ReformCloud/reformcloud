package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.server;

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

public final class PacketOutServerChallengeStart implements Packet {

    public PacketOutServerChallengeStart() {
    }

    public PacketOutServerChallengeStart(String name, byte[] challenge) {
        this.name = name;
        this.challenge = challenge;
    }

    private String name;

    private byte[] challenge;

    public byte[] getChallenge() {
        return challenge;
    }

    @Override
    public int getId() {
        return NetworkUtil.AUTH_BUS + 2;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        authHandler.handle(channel, this, this.name);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeArray(this.challenge);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.name = buffer.readString();
        this.challenge = buffer.readArray();
    }
}
