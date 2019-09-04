package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutGetPlugins extends DefaultPacket {

    public ExternalAPIPacketOutGetPlugins(String process) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 30, new JsonConfiguration().add("process", process));
    }
}
