package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;

public final class ExternalAPIPacketOutUpdateProcessInformation extends DefaultPacket {

    public ExternalAPIPacketOutUpdateProcessInformation(ProcessInformation processInformation) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 37, new JsonConfiguration().add("info", processInformation));
    }
}
