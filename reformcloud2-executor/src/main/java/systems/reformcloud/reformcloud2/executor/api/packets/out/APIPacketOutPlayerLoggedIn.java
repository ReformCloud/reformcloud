package systems.reformcloud.reformcloud2.executor.api.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class APIPacketOutPlayerLoggedIn extends DefaultPacket {

    public APIPacketOutPlayerLoggedIn(String name) {
        super(NetworkUtil.PLAYER_INFORMATION_BUS + 2, new JsonConfiguration().add("name", name));
    }
}
