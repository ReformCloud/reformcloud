package systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public final class ControllerEventProcessStarted extends JsonPacket {

    public ControllerEventProcessStarted(ProcessInformation processInformation) {
        super(NetworkUtil.EVENT_BUS + 2, new JsonConfiguration().add("info", processInformation));
    }
}
