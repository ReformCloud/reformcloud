package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutGetProcessGroup extends JsonPacket {

    public ExternalAPIPacketOutGetProcessGroup(String name) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 21, new JsonConfiguration().add("name", name));
    }
}
