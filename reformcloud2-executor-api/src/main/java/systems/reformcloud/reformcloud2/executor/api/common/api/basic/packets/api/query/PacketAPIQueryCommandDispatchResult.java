package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;

import java.util.Collection;

public class PacketAPIQueryCommandDispatchResult extends QueryResultPacket {

    public PacketAPIQueryCommandDispatchResult() {
    }

    public PacketAPIQueryCommandDispatchResult(Collection<String> result) {
        this.result = result;
    }

    private Collection<String> result;

    public Collection<String> getResult() {
        return result;
    }

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_QUERY_RESULT_ID + 10;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeStringArray(this.result);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.result = buffer.readStringArray();
    }
}
