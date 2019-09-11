package de.klaro.reformcloud2.executor.client.packet.out;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public final class ClientPacketOutAddScreenLine extends DefaultPacket {

    public ClientPacketOutAddScreenLine(UUID uuid, String line) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 9, new JsonConfiguration()
                .add("uuid", uuid)
                .add("line", line)
        );
    }
}
