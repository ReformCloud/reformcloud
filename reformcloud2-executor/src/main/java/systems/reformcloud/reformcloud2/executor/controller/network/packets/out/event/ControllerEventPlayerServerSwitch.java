package systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public class ControllerEventPlayerServerSwitch extends JsonPacket {

    public ControllerEventPlayerServerSwitch(UUID uuid, String target) {
        super(NetworkUtil.EVENT_BUS + 6, new JsonConfiguration().add("uuid", uuid).add("target", target));
    }
}
