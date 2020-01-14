package systems.reformcloud.reformcloud2.executor.api.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public final class APIPacketOutLogoutPlayer extends JsonPacket {

    public APIPacketOutLogoutPlayer(UUID uuid, String name) {
        super(NetworkUtil.PLAYER_INFORMATION_BUS + 3, new JsonConfiguration()
                .add("name", name)
                .add("uuid", uuid)
        );
    }
}
