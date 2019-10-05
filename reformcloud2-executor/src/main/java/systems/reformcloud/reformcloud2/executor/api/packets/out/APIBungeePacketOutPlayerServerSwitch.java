package systems.reformcloud.reformcloud2.executor.api.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public class APIBungeePacketOutPlayerServerSwitch extends DefaultPacket {

    public APIBungeePacketOutPlayerServerSwitch(UUID uuid, String target) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 12, new JsonConfiguration().add("uuid", uuid).add("target", target));
    }
}
