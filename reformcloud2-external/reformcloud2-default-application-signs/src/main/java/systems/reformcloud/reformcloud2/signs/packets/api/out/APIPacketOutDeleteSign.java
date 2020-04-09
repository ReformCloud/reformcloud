package systems.reformcloud.reformcloud2.signs.packets.api.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

import java.util.Collection;

public class APIPacketOutDeleteSign extends JsonPacket {

    public APIPacketOutDeleteSign(CloudSign sign) {
        super(PacketUtil.SIGN_BUS + 1, new JsonConfiguration().add("sign", sign));
    }

    public APIPacketOutDeleteSign(Collection<CloudSign> signs) {
        super(PacketUtil.SIGN_BUS + 1, new JsonConfiguration().add("signs", signs));
    }
}
