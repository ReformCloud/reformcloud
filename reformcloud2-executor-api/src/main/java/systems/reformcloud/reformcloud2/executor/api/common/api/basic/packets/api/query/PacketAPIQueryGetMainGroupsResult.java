package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;

import java.util.List;

public class PacketAPIQueryGetMainGroupsResult extends QueryResultPacket {

    public PacketAPIQueryGetMainGroupsResult() {
    }

    public PacketAPIQueryGetMainGroupsResult(List<MainGroup> mainGroups) {
        this.mainGroups = mainGroups;
    }

    private List<MainGroup> mainGroups;

    public List<MainGroup> getMainGroups() {
        return mainGroups;
    }

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_QUERY_RESULT_ID + 4;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObjects(this.mainGroups);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.mainGroups = buffer.readObjects(MainGroup.class);
    }
}
