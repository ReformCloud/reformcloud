package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.MainGroup;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutCreateMainGroup extends DefaultPacket {

    public ExternalAPIPacketOutCreateMainGroup(MainGroup mainGroup) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 16, new JsonConfiguration()
                .add("group", mainGroup)
        );
    }
}
