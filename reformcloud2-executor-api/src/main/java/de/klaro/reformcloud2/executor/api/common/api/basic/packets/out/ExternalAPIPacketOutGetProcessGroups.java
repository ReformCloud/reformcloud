package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutGetProcessGroups extends DefaultPacket {

    public ExternalAPIPacketOutGetProcessGroups() {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 25, new JsonConfiguration());
    }
}
