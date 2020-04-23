package systems.reformcloud.reformcloud2.executor.api.common.network.packet.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;

public abstract class QueryPacket implements Packet {

    protected UUID uniqueID;

    @NotNull
    public UUID getQueryUniqueId() {
        return this.uniqueID;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeUniqueId(this.uniqueID);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.uniqueID = buffer.readUniqueId();
    }
}
