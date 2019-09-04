package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.MainGroup;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutUpdateMainGroup extends DefaultPacket {

    public ExternalAPIPacketOutUpdateMainGroup(MainGroup group) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 18, new JsonConfiguration().add("group", group));
    }
}
