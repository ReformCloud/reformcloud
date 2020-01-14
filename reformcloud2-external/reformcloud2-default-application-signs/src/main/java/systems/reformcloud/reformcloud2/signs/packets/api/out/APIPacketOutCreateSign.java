package systems.reformcloud.reformcloud2.signs.packets.api.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class APIPacketOutCreateSign extends JsonPacket {

    public APIPacketOutCreateSign(CloudSign sign) {
        super(PacketUtil.SIGN_BUS + 2, new JsonConfiguration().add("sign", sign));
    }
}
