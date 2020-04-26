package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;

public class PacketAPIQueryRequestMainGroupResult extends QueryResultPacket {

    public PacketAPIQueryRequestMainGroupResult() {
    }

    public PacketAPIQueryRequestMainGroupResult(MainGroup mainGroup) {
        this.mainGroup = mainGroup;
    }

    private MainGroup mainGroup;

    public MainGroup getMainGroup() {
        return mainGroup;
    }

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_QUERY_RESULT_ID + 7;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.mainGroup);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.mainGroup = buffer.readObject(MainGroup.class);
    }
}
