package de.klaro.reformcloud2.executor.api.packets.out;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class APIBungeePacketOutRequestIngameMessages extends DefaultPacket {

    public APIBungeePacketOutRequestIngameMessages() {
        super(NetworkUtil.CONTROLLER_QUERY_BUS + 2, new JsonConfiguration());
    }
}
