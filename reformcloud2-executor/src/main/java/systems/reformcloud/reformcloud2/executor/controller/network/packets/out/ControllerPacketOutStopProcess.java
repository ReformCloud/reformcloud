package systems.reformcloud.reformcloud2.executor.controller.network.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public final class ControllerPacketOutStopProcess extends JsonPacket {

    public ControllerPacketOutStopProcess(UUID uniqueID) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 3, new JsonConfiguration().add("uniqueID", uniqueID));
    }
}
