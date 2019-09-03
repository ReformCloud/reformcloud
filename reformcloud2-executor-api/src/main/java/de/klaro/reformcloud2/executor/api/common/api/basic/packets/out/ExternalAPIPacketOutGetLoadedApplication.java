package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutGetLoadedApplication extends DefaultPacket {

    public ExternalAPIPacketOutGetLoadedApplication(String name) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 3, new JsonConfiguration().add("name", name));
    }
}
