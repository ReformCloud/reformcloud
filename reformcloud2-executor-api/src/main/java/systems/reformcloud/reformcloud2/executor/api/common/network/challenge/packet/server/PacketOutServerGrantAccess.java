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

public final class PacketOutServerGrantAccess implements Packet {

    public PacketOutServerGrantAccess() {
    }

    public PacketOutServerGrantAccess(String name, boolean access) {
        this.name = name;
        this.access = access;
    }

    private String name;

    private boolean access;

    public boolean isAccess() {
        return access;
    }

    @Override
    public int getId() {
        return NetworkUtil.AUTH_BUS + 4;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        parent.auth = authHandler.handle(channel, this, this.name);
        if (parent.auth) {
            reader.setChannelHandlerContext(channel, this.name);
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeBoolean(this.access);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.name = buffer.readString();
        this.access = buffer.readBoolean();
    }
}
