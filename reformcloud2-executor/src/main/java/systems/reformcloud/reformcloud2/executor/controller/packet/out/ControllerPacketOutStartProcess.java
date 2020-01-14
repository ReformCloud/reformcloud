package systems.reformcloud.reformcloud2.executor.controller.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public final class ControllerPacketOutStartProcess extends JsonPacket {

    public ControllerPacketOutStartProcess(ProcessInformation processInformation) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 2, new JsonConfiguration().add("info", processInformation));
    }
}
