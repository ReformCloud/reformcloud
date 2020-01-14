package systems.reformcloud.reformcloud2.executor.client.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public final class ClientPacketOutAddScreenLine extends JsonPacket {

    public ClientPacketOutAddScreenLine(UUID uuid, String line) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 9, new JsonConfiguration()
                .add("uuid", uuid)
                .add("line", line)
        );
    }
}
