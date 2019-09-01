package de.klaro.reformcloud2.executor.controller.packet.out.api;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ControllerExecuteCommand extends DefaultPacket {

    public ControllerExecuteCommand(String command) {
        super(48, new JsonConfiguration().add("command", command));
    }
}
