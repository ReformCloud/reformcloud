package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public final class ExternalAPIPacketOutGetProcess extends DefaultPacket {

    public ExternalAPIPacketOutGetProcess(String name) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 33, new JsonConfiguration().add("name", name));
    }

    public ExternalAPIPacketOutGetProcess(UUID uniqueID) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 33, new JsonConfiguration().add("uniqueID", uniqueID));
    }
}
