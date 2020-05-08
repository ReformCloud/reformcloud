package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerServerSwitchEvent;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;

public final class EventPacketPlayerServerSwitch extends Packet {

    public EventPacketPlayerServerSwitch() {
    }

    public EventPacketPlayerServerSwitch(UUID uniqueId, String name, String previousServer, String targetServer) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.targetServer = targetServer;
        this.previousServer = previousServer;
    }

    private UUID uniqueId;

    private String name;

    private String targetServer;

    private String previousServer;

    @Override
    public int getId() {
        return NetworkUtil.EVENT_BUS + 6;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        ExternalEventBusHandler.getInstance().callEvent(new PlayerServerSwitchEvent(this.uniqueId, this.name, this.previousServer, this.targetServer));
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.uniqueId);
        buffer.writeString(this.name);
        buffer.writeString(this.targetServer);
        buffer.writeString(this.previousServer);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.uniqueId = buffer.readUniqueId();
        this.name = buffer.readString();
        this.targetServer = buffer.readString();
        this.previousServer = buffer.readString();
    }
}
