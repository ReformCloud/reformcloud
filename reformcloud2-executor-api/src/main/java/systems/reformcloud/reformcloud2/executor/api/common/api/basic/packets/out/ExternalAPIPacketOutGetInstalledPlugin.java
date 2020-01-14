package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutGetInstalledPlugin extends JsonPacket {

    public ExternalAPIPacketOutGetInstalledPlugin(String name, String process) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 29, new JsonConfiguration().add("name", name).add("process", process));
    }
}
