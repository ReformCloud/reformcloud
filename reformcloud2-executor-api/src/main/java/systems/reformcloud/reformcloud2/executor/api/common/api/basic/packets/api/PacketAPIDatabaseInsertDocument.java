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

public class PacketAPIDatabaseInsertDocument implements Packet {

    public PacketAPIDatabaseInsertDocument() {
    }

    public PacketAPIDatabaseInsertDocument(String database, String keyEntry, String identifier, JsonConfiguration configuration) {
        this.database = database;
        this.keyEntry = keyEntry;
        this.identifier = identifier;
        this.configuration = configuration;
    }

    private String database;

    private String keyEntry;

    private String identifier;

    private JsonConfiguration configuration;

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 11;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        ExecutorAPI.getInstance().getSyncAPI().getDatabaseSyncAPI().insert(
                this.database, this.keyEntry, this.identifier, this.configuration
        );
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeString(this.database);
        buffer.writeString(this.keyEntry);
        buffer.writeString(this.identifier);
        buffer.writeArray(this.configuration.toPrettyBytes());
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.database = buffer.readString();
        this.keyEntry = buffer.readString();
        this.identifier = buffer.readString();

        try (InputStream inputStream = new ByteArrayInputStream(buffer.readArray())) {
            this.configuration = new JsonConfiguration(inputStream);
        } catch (final IOException ex) {
            this.configuration = new JsonConfiguration();
            ex.printStackTrace();
        }
    }
}
