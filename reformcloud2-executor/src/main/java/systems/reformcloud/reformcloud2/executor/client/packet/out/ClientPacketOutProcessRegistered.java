package systems.reformcloud.reformcloud2.executor.client.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public final class ClientPacketOutProcessRegistered extends DefaultPacket {

    public ClientPacketOutProcessRegistered(UUID uuid, String name) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 5, new JsonConfiguration().add("uuid", uuid).add("name", name));
    }
}
