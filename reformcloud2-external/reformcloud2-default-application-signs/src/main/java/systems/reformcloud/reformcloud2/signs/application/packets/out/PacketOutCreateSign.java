package systems.reformcloud.reformcloud2.signs.application.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.signs.packets.PacketUtil;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;

public class PacketOutCreateSign extends JsonPacket {

    public PacketOutCreateSign(CloudSign cloudSign) {
        super(PacketUtil.SIGN_BUS + 4, new JsonConfiguration().add("sign", cloudSign));
    }
}
