package de.klaro.reformcloud2.executor.client.packet.out;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public final class ClientPacketOutProcessPrepared extends DefaultPacket {

    public ClientPacketOutProcessPrepared(String name, UUID uuid, String template) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 6, new JsonConfiguration()
                .add("name", name)
                .add("uuid", uuid)
                .add("template", template)
        );
    }
}
