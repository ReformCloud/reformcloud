package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutGetCommand extends JsonPacket {

    public ExternalAPIPacketOutGetCommand(String name) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 9, new JsonConfiguration().add("name", name));
    }
}
