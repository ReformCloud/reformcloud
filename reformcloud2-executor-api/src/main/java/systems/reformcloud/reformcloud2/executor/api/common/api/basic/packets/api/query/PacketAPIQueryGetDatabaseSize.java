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

public class PacketAPIQueryGetDatabaseSize extends Packet {

    public PacketAPIQueryGetDatabaseSize() {
    }

    public PacketAPIQueryGetDatabaseSize(String databaseName) {
        this.databaseName = databaseName;
    }

    private String databaseName;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 54;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        sender.sendQueryResult(
                this.getQueryUniqueID(),
                new PacketAPIQueryGetDatabaseSizeResult(ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().size(this.databaseName))
        );
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.databaseName);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.databaseName = buffer.readString();
    }
}
