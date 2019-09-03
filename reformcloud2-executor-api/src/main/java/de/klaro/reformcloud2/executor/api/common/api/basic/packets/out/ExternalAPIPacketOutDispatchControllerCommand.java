package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutDispatchControllerCommand extends DefaultPacket {

    public ExternalAPIPacketOutDispatchControllerCommand(String commandLine) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 8, new JsonConfiguration().add("command", commandLine));
    }
}
