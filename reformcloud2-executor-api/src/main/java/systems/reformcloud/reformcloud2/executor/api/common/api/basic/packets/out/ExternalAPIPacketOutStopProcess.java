package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public final class ExternalAPIPacketOutStopProcess extends JsonPacket {

    public ExternalAPIPacketOutStopProcess(String name) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 32, new JsonConfiguration().add("name", name));
    }

    public ExternalAPIPacketOutStopProcess(UUID uniqueID) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 32, new JsonConfiguration().add("uniqueID", uniqueID));
    }
}
