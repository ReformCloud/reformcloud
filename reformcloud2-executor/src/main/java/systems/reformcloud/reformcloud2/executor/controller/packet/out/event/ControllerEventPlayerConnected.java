package systems.reformcloud.reformcloud2.executor.controller.packet.out.event;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public class ControllerEventPlayerConnected extends JsonPacket {

    public ControllerEventPlayerConnected(String name) {
        super(NetworkUtil.EVENT_BUS + 4, new JsonConfiguration().add("name", name));
    }
}
