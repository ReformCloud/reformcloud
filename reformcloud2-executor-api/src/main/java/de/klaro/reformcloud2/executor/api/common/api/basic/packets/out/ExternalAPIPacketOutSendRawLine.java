package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutSendRawLine extends DefaultPacket {

    public ExternalAPIPacketOutSendRawLine(String line) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 7, new JsonConfiguration().add("line", line));
    }
}
