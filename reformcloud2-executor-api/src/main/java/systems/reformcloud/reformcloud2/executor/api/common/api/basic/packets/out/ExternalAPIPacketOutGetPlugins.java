package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutGetPlugins extends JsonPacket {

    public ExternalAPIPacketOutGetPlugins(String process) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 30, new JsonConfiguration().add("process", process));
    }
}
