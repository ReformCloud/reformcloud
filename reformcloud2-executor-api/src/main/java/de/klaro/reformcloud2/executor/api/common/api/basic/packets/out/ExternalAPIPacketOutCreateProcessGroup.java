package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.ProcessGroup;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutCreateProcessGroup extends DefaultPacket {

    public ExternalAPIPacketOutCreateProcessGroup(ProcessGroup group) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 17, new JsonConfiguration().add("group", group));
    }
}
