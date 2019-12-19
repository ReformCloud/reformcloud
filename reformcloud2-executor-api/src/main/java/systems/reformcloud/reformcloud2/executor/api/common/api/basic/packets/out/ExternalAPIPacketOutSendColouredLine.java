package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutSendColouredLine extends DefaultPacket {

    public ExternalAPIPacketOutSendColouredLine(String line) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 6, new JsonConfiguration().add("line", line));
    }
}
