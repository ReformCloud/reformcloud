package systems.reformcloud.reformcloud2.executor.api.network.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public final class APIPacketOutCreateLoginRequest extends JsonPacket {

    public APIPacketOutCreateLoginRequest(UUID uniqueID, String name) {
        super(NetworkUtil.PLAYER_INFORMATION_BUS + 1, new JsonConfiguration()
                .add("uniqueID", uniqueID)
                .add("name", name)
        );
    }
}
