package systems.reformcloud.reformcloud2.executor.api.common.network.challenge.packet.server;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.PacketCallable;

public class PacketOutServerChallengeStart implements Packet {

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

    @NotNull
    @Override
    public PacketCallable onPacketReceive() {
        return (reader, authHandler, parent, sender) -> authHandler.handle(sender.getChannelContext(), this, this.name);
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
