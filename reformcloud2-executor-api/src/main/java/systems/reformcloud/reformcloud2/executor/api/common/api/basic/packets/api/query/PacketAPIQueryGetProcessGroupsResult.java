package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;

import java.util.List;

public class PacketAPIQueryGetProcessGroupsResult extends QueryResultPacket {

    public PacketAPIQueryGetProcessGroupsResult() {
    }

    public PacketAPIQueryGetProcessGroupsResult(List<ProcessGroup> processGroups) {
        this.processGroups = processGroups;
    }

    private List<ProcessGroup> processGroups;

    public List<ProcessGroup> getProcessGroups() {
        return processGroups;
    }

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_QUERY_RESULT_ID + 6;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObjects(this.processGroups);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processGroups = buffer.readObjects(ProcessGroup.class);
    }
}
