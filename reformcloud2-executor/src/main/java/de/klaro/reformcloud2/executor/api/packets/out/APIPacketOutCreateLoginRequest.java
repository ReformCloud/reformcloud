package de.klaro.reformcloud2.executor.api.packets.out;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public final class APIPacketOutCreateLoginRequest extends DefaultPacket {

    public APIPacketOutCreateLoginRequest(UUID uniqueID, String name) {
        super(NetworkUtil.PLAYER_INFORMATION_BUS + 1, new JsonConfiguration()
                .add("uniqueID", uniqueID)
                .add("name", name)
        );
    }
}
