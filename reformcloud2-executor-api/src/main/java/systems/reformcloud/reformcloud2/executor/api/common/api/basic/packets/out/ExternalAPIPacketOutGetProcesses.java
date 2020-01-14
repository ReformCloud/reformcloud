package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutGetProcesses extends JsonPacket {

    public ExternalAPIPacketOutGetProcesses() {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 34, new JsonConfiguration());
    }

    public ExternalAPIPacketOutGetProcesses(String group) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 34, new JsonConfiguration().add("group", group));
    }
}
