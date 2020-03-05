package systems.reformcloud.reformcloud2.executor.api.network.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public class APIBungeePacketOutRequestIngameMessages extends JsonPacket {

    public APIBungeePacketOutRequestIngameMessages() {
        super(NetworkUtil.CONTROLLER_QUERY_BUS + 2, new JsonConfiguration());
    }
}
