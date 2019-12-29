package systems.reformcloud.reformcloud2.signs.packets.api.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;

public class APIPacketOutRequestSignLayouts extends JsonPacket {

    public APIPacketOutRequestSignLayouts() {
        super(PacketUtil.SIGN_BUS + 3, new JsonConfiguration());
    }
}
