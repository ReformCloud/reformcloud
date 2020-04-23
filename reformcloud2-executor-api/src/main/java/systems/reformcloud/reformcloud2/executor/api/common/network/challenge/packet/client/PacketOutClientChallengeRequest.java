package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.client;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.PacketCallable;

public class PacketOutClientChallengeRequest implements Packet {

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

    @NotNull
    @Override
    public PacketCallable onPacketReceive() {
        return (reader, authHandler, parent, sender) -> {
            if (DefaultChannelManager.INSTANCE.get(this.name).isPresent()) {
                System.out.println("Unknown connect from channel (Name=" + this.name + "). If the name is null, that might be an attack");
                sender.close();
                return;
            }

            parent.auth = authHandler.handle(sender.getChannelContext(), this, this.name);
            if (parent.auth) {
                reader.setChannelHandlerContext(sender.getChannelContext(), this.name);
            }
        };
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
