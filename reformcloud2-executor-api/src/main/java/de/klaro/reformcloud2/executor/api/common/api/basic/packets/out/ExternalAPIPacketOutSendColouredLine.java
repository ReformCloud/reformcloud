package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutSendColouredLine extends DefaultPacket {

    public ExternalAPIPacketOutSendColouredLine(String line) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 6, new JsonConfiguration().add("line", line));
    }
}
