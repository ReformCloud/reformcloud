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

public class PacketAPIQueryDatabaseGetDocument implements Packet {

    public PacketAPIQueryDatabaseGetDocument() {
    }

    public PacketAPIQueryDatabaseGetDocument(String database, String entryKey, String identifier) {
        this.database = database;
        this.entryKey = entryKey;
        this.identifier = identifier;
    }

    private String database;

    private String entryKey;

    private String identifier;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 10;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (sender == null) {
            return;
        }

        sender.sendQueryResult(
                this.getQueryUniqueID(),
                new PacketAPIQueryDatabaseGetDocumentResult(ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().find(this.database, this.entryKey, this.identifier))
        );
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.database);
        buffer.writeString(this.entryKey);
        buffer.writeString(this.identifier);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.database = buffer.readString();
        this.entryKey = buffer.readString();
        this.identifier = buffer.readString();
    }
}
