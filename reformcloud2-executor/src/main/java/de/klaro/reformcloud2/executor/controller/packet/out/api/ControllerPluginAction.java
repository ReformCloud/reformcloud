package de.klaro.reformcloud2.executor.controller.packet.out.api;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ControllerPluginAction extends DefaultPacket {

    public ControllerPluginAction(Action action, Object data) {
        super(47, new JsonConfiguration().add("action", action).add("data", data));
    }

    public enum Action {

        INSTALL,

        UNINSTALL
    }
}
