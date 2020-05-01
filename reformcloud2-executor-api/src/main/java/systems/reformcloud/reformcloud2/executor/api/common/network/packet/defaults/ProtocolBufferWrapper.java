package systems.reformcloud.reformcloud2.executor.api.common.network.packet.defaults;

import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;

import java.util.UUID;

public class ProtocolBufferWrapper {

    public ProtocolBufferWrapper(int id, UUID uuid, ProtocolBuffer protocolBuffer) {
        this.id = id;
        this.uuid = uuid;
        this.protocolBuffer = protocolBuffer;
    }

    private final int id;

    private final UUID uuid;

    private final ProtocolBuffer protocolBuffer;

    public int getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public ProtocolBuffer getProtocolBuffer() {
        return protocolBuffer;
    }
}
