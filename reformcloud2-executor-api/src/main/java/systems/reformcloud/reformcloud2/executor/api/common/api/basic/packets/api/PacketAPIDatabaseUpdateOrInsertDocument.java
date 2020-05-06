package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PacketAPIDatabaseUpdateOrInsertDocument extends Packet {

    public PacketAPIDatabaseUpdateOrInsertDocument() {
    }

    public PacketAPIDatabaseUpdateOrInsertDocument(String databaseName, String entryKey, String identifier, JsonConfiguration data) {
        this.databaseName = databaseName;
        this.entryKey = entryKey;
        this.identifier = identifier;
        this.data = data;
    }

    private String databaseName;

    private String entryKey;

    private String identifier;

    private JsonConfiguration data;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 12;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        if (this.entryKey != null) {
            ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().update(this.databaseName, this.entryKey, this.data);
            return;
        }

        if (this.identifier != null) {
            ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().updateIfAbsent(this.databaseName, this.identifier, this.data);
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.databaseName);
        buffer.writeString(this.entryKey);
        buffer.writeString(this.identifier);
        buffer.writeArray(this.data.toPrettyBytes());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.databaseName = buffer.readString();
        this.entryKey = buffer.readString();
        this.identifier = buffer.readString();

        try (InputStream inputStream = new ByteArrayInputStream(buffer.readArray())) {
            this.data = new JsonConfiguration(inputStream);
        } catch (final IOException ex) {
            this.data = new JsonConfiguration();
            ex.printStackTrace();
        }
    }
}
