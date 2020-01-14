package systems.reformcloud.reformcloud2.executor.client.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public final class ClientPacketOutProcessStopped extends JsonPacket {

    public ClientPacketOutProcessStopped(UUID uuid, String name) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 1, new JsonConfiguration().add("uuid", uuid).add("name", name));
    }
}
