package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutGetInstalledPlugin extends DefaultPacket {

    public ExternalAPIPacketOutGetInstalledPlugin(String name, String process) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 29, new JsonConfiguration().add("name", name).add("process", process));
    }
}
