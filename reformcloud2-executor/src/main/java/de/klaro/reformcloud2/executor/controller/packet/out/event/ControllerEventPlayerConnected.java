package de.klaro.reformcloud2.executor.controller.packet.out.event;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class ControllerEventPlayerConnected extends DefaultPacket {

    public ControllerEventPlayerConnected(String name) {
        super(NetworkUtil.EVENT_BUS + 4, new JsonConfiguration().add("name", name));
    }
}
