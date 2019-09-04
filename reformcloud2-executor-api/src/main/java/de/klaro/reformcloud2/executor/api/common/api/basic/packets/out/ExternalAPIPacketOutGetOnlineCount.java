package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.Collection;

public final class ExternalAPIPacketOutGetOnlineCount extends DefaultPacket {

    public ExternalAPIPacketOutGetOnlineCount(Collection<String> ignoredProxies) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 36, new JsonConfiguration().add("ignored", ignoredProxies));
    }
}
