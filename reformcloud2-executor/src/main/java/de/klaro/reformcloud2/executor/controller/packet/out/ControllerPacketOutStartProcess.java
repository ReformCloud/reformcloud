package de.klaro.reformcloud2.executor.controller.packet.out;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.process.ProcessInformation;

public final class ControllerPacketOutStartProcess extends DefaultPacket {

    public ControllerPacketOutStartProcess(ProcessInformation processInformation) {
        super(NetworkUtil.CONTROLLER_INFORMATION_PACKETS + 2, new JsonConfiguration().add("info", processInformation));
    }
}
