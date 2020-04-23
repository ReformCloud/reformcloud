package systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults;

import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

public class ProtocolBufferWrapper {

    public ProtocolBufferWrapper(int id, ProtocolBuffer protocolBuffer) {
        this.id = id;
        this.protocolBuffer = protocolBuffer;
    }

    private final int id;

    private final ProtocolBuffer protocolBuffer;

    public int getId() {
        return id;
    }

    public ProtocolBuffer getProtocolBuffer() {
        return protocolBuffer;
    }
}
