package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;

public class PacketAPIQueryGetDatabaseSizeResult extends QueryResultPacket {

    public PacketAPIQueryGetDatabaseSizeResult(int size) {
        this.size = size;
    }

    private int size;

    public int getSize() {
        return size;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeVarInt(this.size);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.size = buffer.readVarInt();
    }
}
