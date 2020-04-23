package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerLogoutEvent;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;

public final class EventPacketLogoutPlayer implements Packet {

    public EventPacketLogoutPlayer() {
    }

    public EventPacketLogoutPlayer(String name, UUID uniqueID) {
        this.name = name;
        this.uniqueID = uniqueID;
    }

    private String name;

    private UUID uniqueID;

    @Override
    public int getId() {
        return NetworkUtil.EVENT_BUS + 5;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        ExternalEventBusHandler.getInstance().callEvent(new PlayerLogoutEvent(this.name, this.uniqueID));
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeUniqueId(this.uniqueID);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.name = buffer.readString();
        this.uniqueID = buffer.readUniqueId();
    }
}
