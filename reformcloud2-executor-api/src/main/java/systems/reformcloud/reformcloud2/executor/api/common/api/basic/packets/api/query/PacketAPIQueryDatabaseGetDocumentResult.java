package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PacketAPIQueryDatabaseGetDocumentResult extends QueryResultPacket {

    public PacketAPIQueryDatabaseGetDocumentResult() {
    }

    public PacketAPIQueryDatabaseGetDocumentResult(JsonConfiguration result) {
        this.result = result;
    }

    private JsonConfiguration result;

    public JsonConfiguration getResult() {
        return result;
    }

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_QUERY_RESULT_ID + 2;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeBoolean(this.result == null);
        if (this.result != null) {
            buffer.writeArray(this.result.toPrettyBytes());
        }
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        if (buffer.readBoolean()) {
            return;
        }

        try (InputStream inputStream = new ByteArrayInputStream(buffer.readArray())) {
            this.result = new JsonConfiguration(inputStream);
        } catch (final IOException ex) {
            this.result = new JsonConfiguration();
            ex.printStackTrace();
        }
    }
}
