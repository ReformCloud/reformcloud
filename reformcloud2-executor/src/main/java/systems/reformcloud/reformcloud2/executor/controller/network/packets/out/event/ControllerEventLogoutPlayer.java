package systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public class ControllerEventLogoutPlayer extends JsonPacket {

    public ControllerEventLogoutPlayer(String name, UUID uuid) {
        super(NetworkUtil.EVENT_BUS + 5, new JsonConfiguration().add("name", name).add("uuid", uuid));
    }
}
