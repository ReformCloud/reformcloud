package systems.reformcloud.reformcloud2.executor.controller.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public final class ControllerPacketOutCopyProcess extends DefaultPacket {

    public ControllerPacketOutCopyProcess(UUID uuid) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 8, new JsonConfiguration().add("uuid", uuid));
    }
}
