package systems.reformcloud.reformcloud2.executor.api.common.network.messaging;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ChannelMessageReceivedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.PacketCallable;

public class ProxiedChannelMessageHandler implements Packet {

    public ProxiedChannelMessageHandler() {
    }

    public ProxiedChannelMessageHandler(JsonConfiguration message, String base, String sub) {
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

    @NotNull
    @Override
    public PacketCallable onPacketReceive() {
        return (reader, authHandler, sender) -> {
            ExecutorAPI.getInstance().getEventManager().callEvent(new ChannelMessageReceivedEvent(this.message, this.base, this.sub));
        };
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
