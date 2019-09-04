package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.Configurable;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutStartProcess extends DefaultPacket {

    public ExternalAPIPacketOutStartProcess(String group, String template, Configurable data) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 31, new JsonConfiguration()
                .add("group", group)
                .add("template", template == null ? "" : template)
                .add("extra", data)
        );
    }
}
