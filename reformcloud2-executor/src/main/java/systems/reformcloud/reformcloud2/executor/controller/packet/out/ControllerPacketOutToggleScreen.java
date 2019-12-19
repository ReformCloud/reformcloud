package systems.reformcloud.reformcloud2.executor.controller.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public final class ControllerPacketOutToggleScreen extends DefaultPacket {

    public ControllerPacketOutToggleScreen(UUID process) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 10, new JsonConfiguration().add("uuid", process));
    }
}
