package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.groups.ProcessGroup;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutUpdateProcessGroup extends DefaultPacket {

    public ExternalAPIPacketOutUpdateProcessGroup(ProcessGroup processGroup) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 19, new JsonConfiguration().add("group", processGroup));
    }
}
