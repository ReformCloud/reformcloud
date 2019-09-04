package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutGetProcesses extends DefaultPacket {

    public ExternalAPIPacketOutGetProcesses() {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 34, new JsonConfiguration());
    }

    public ExternalAPIPacketOutGetProcesses(String group) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 34, new JsonConfiguration().add("group", group));
    }
}
