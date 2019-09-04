package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutGetMainGroups extends DefaultPacket {

    public ExternalAPIPacketOutGetMainGroups() {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 24, new JsonConfiguration());
    }
}
