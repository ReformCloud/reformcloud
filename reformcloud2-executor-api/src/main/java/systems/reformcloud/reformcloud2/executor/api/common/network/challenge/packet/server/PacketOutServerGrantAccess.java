package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.server;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.PacketCallable;

public class PacketOutServerGrantAccess implements Packet {

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

    @NotNull
    @Override
    public PacketCallable onPacketReceive() {
        return (reader, authHandler, parent, sender) -> authHandler.handle(sender.getChannelContext(), this, this.name);
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
