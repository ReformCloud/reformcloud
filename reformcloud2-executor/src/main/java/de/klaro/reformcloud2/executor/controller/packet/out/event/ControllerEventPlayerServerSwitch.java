package de.klaro.reformcloud2.executor.controller.packet.out.event;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public class ControllerEventPlayerServerSwitch extends DefaultPacket {

    public ControllerEventPlayerServerSwitch(UUID uuid, String target) {
        super(NetworkUtil.EVENT_BUS + 6, new JsonConfiguration().add("uuid", uuid).add("target", target));
    }
}