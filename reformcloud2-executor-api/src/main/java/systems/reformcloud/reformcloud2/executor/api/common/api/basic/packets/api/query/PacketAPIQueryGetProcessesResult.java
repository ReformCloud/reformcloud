package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.api.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.query.QueryResultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.List;

public class PacketAPIQueryGetProcessesResult extends QueryResultPacket {

    public PacketAPIQueryGetProcessesResult() {
    }

    public PacketAPIQueryGetProcessesResult(List<ProcessInformation> processInformation) {
        this.processInformation = processInformation;
    }

    private List<ProcessInformation> processInformation;

    public List<ProcessInformation> getProcessInformation() {
        return processInformation;
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeObjects(this.processInformation);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processInformation = buffer.readObjects(ProcessInformation.class);
    }
}
