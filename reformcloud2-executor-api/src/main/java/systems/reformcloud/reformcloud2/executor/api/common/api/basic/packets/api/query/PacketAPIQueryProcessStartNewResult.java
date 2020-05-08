package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public class PacketAPIQueryProcessStartNewResult extends QueryResultPacket {

    public PacketAPIQueryProcessStartNewResult() {
    }

    public PacketAPIQueryProcessStartNewResult(ProcessInformation processInformation) {
        this.processInformation = processInformation;
    }

    private ProcessInformation processInformation;

    public ProcessInformation getProcessInformation() {
        return processInformation;
    }

    @Override
    public int getId() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_QUERY_RESULT_ID + 11;
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
