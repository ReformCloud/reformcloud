package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query;

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

public class PacketAPIQueryGetProcessesPerGroup extends Packet {

    public PacketAPIQueryGetProcessesPerGroup() {
    }

    public PacketAPIQueryGetProcessesPerGroup(String groupName) {
        this.groupName = groupName;
    }

    private String groupName;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 51;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        sender.sendQueryResult(
                this.getQueryUniqueID(),
                new PacketAPIQueryGetProcessesResult(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcesses(this.groupName))
        );
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.groupName);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.groupName = buffer.readString();
    }
}
