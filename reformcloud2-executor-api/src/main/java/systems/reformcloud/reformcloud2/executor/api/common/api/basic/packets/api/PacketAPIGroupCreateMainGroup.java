package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.List;

public class PacketAPIGroupCreateMainGroup implements Packet {

    public PacketAPIGroupCreateMainGroup() {
    }

    public PacketAPIGroupCreateMainGroup(String name, List<String> subGroups) {
        this.name = name;
        this.subGroups = subGroups;
    }

    private String name;

    private List<String> subGroups;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 16;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().createMainGroup(this.name, this.subGroups);
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeStringArray(this.subGroups);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.name = buffer.readString();
        this.subGroups = buffer.readStringArray();
    }
}
