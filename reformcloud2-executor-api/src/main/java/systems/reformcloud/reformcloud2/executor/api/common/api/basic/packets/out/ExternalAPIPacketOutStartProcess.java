package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutStartProcess extends JsonPacket {

    public ExternalAPIPacketOutStartProcess(String group, String template, JsonConfiguration data) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 31, new JsonConfiguration()
                .add("group", group)
                .add("template", template == null ? "" : template)
                .add("extra", data)
        );
    }
}
