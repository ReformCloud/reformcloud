package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public final class PacketOutClientChallengeRequest extends Packet {

    public PacketOutClientChallengeRequest() {
    }

    public PacketOutClientChallengeRequest(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public int getId() {
        return NetworkUtil.AUTH_BUS + 1;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (DefaultChannelManager.INSTANCE.get(this.name).isPresent()) {
            System.out.println("Unknown connect from channel (Name=" + this.name + "). If the name is null, that might be an attack");
            channel.close();
            return;
        }

        authHandler.handle(channel, this, this.name);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.name);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.name = buffer.readString();
    }
}
