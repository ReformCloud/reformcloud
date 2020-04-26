package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;

public class PacketAPIQueryDatabaseContainsResult extends QueryResultPacket {

    public PacketAPIQueryDatabaseContainsResult() {
    }

    public PacketAPIQueryDatabaseContainsResult(boolean result) {
        this.result = result;
    }

    private boolean result;

    public boolean isResult() {
        return result;
    }

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_QUERY_RESULT_ID + 1;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeBoolean(this.result);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.result = buffer.readBoolean();
    }
}
