package systems.reformcloud.reformcloud2.executor.client.network.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.Collection;
import java.util.UUID;

public class ClientPacketOutScreenEnabled extends JsonPacket {

    public ClientPacketOutScreenEnabled(UUID uniqueID, Collection<String> lines) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 13, new JsonConfiguration().add("uniqueID", uniqueID).add("lines", lines));
    }
}
