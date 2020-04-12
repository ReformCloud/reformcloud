package systems.reformcloud.reformcloud2.executor.controller.network.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public class ControllerPacketOutStartPreparedProcess extends JsonPacket {

    public ControllerPacketOutStartPreparedProcess(ProcessInformation information) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 14, new JsonConfiguration().add("info", information));
    }
}
