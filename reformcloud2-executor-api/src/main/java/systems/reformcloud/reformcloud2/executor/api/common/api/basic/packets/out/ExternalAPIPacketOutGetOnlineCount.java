package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.Collection;

public final class ExternalAPIPacketOutGetOnlineCount extends JsonPacket {

    public ExternalAPIPacketOutGetOnlineCount(Collection<String> ignoredProxies) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 36, new JsonConfiguration().add("ignored", ignoredProxies));
    }
}
