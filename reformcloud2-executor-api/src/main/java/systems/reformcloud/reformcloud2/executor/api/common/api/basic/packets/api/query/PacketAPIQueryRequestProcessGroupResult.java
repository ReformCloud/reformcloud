package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;

public class PacketAPIQueryRequestProcessGroupResult extends QueryResultPacket {

    public PacketAPIQueryRequestProcessGroupResult() {
    }

    public PacketAPIQueryRequestProcessGroupResult(ProcessGroup processGroup) {
        this.processGroup = processGroup;
    }

    private ProcessGroup processGroup;

    public ProcessGroup getProcessGroup() {
        return processGroup;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.processGroup);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processGroup = buffer.readObject(ProcessGroup.class);
    }
}
