package de.klaro.reformcloud2.executor.api.packets.out;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public final class APIPacketOutLogoutPlayer extends DefaultPacket {

    public APIPacketOutLogoutPlayer(UUID uuid, String name) {
        super(NetworkUtil.PLAYER_INFORMATION_BUS + 3, new JsonConfiguration()
                .add("name", name)
                .add("uuid", uuid)
        );
    }
}
