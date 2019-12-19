package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutGetPlugins extends DefaultPacket {

    public ExternalAPIPacketOutGetPlugins(String process) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 30, new JsonConfiguration().add("process", process));
    }
}
