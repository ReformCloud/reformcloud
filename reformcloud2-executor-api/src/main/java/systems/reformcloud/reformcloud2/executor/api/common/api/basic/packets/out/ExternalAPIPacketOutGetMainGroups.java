package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutGetMainGroups extends JsonPacket {

    public ExternalAPIPacketOutGetMainGroups() {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 24, new JsonConfiguration());
    }
}
