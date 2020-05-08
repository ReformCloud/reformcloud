package systems.reformcloud.reformcloud2.executor.api.common.network.messaging;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ChannelMessageReceivedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public class ProxiedChannelMessage extends Packet {

    public ProxiedChannelMessage() {
    }

    public ProxiedChannelMessage(JsonConfiguration message, String base, String sub) {
        this.message = message;
        this.base = base;
        this.sub = sub;
    }

    private JsonConfiguration message;

    private String base;

    private String sub;

    @Override
    public int getId() {
        return NetworkUtil.MESSAGING_BUS + 3;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        ExecutorAPI.getInstance().getEventManager().callEvent(new ChannelMessageReceivedEvent(this.message, this.base, this.sub));
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeBytes(message.toPrettyBytes());
        buffer.writeString(this.base);
        buffer.writeString(this.sub);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.message = new JsonConfiguration(buffer.readArray());
        this.base = buffer.readString();
        this.sub = buffer.readString();
    }
}
