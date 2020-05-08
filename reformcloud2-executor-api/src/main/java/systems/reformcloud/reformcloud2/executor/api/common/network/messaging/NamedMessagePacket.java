package systems.reformcloud.reformcloud2.executor.api.common.network.messaging;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class NamedMessagePacket extends Packet {

    public NamedMessagePacket() {
    }

    public NamedMessagePacket(Collection<String> receivers, JsonConfiguration content,
                              ErrorReportHandling handling, String baseChannel, String subChannel) {
        this.receivers = receivers;
        this.content = content;
        this.handling = handling;
        this.baseChannel = baseChannel;
        this.subChannel = subChannel;
    }

    private Collection<String> receivers;

    private JsonConfiguration content;

    private ErrorReportHandling handling;

    private String baseChannel;

    private String subChannel;

    @Override
    public int getId() {
        return NetworkUtil.MESSAGING_BUS + 2;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        DefaultChannelManager.INSTANCE.broadcast(
                new ProxiedChannelMessage(this.content, this.baseChannel, this.subChannel),
                e -> this.receivers.contains(e.getName())
        );
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeStringArray(this.receivers);
        buffer.writeArray(content.toPrettyBytes());
        buffer.writeVarInt(this.handling.ordinal());
        buffer.writeString(this.baseChannel);
        buffer.writeString(this.subChannel);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.receivers = buffer.readStringArray();

        try (InputStream stream = new ByteArrayInputStream(buffer.readArray())) {
            this.content = new JsonConfiguration(stream);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        this.handling = ErrorReportHandling.values()[buffer.readVarInt()];
        this.baseChannel = buffer.readString();
        this.subChannel = buffer.readString();
    }
}
