package systems.reformcloud.reformcloud2.executor.node.network.packet.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public class PacketNodeQueryStartProcessResult extends QueryResultPacket {

    public PacketNodeQueryStartProcessResult() {
    }

    public PacketNodeQueryStartProcessResult(ProcessInformation processInformation) {
        this.processInformation = processInformation;
    }

    private ProcessInformation processInformation;

    public ProcessInformation getProcessInformation() {
        return processInformation;
    }

    @Override
    public int getId() {
        return NetworkUtil.NODE_TO_NODE_QUERY_BUS + 2;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObject(this.processInformation);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processInformation = buffer.readObject(ProcessInformation.class);
    }
}
