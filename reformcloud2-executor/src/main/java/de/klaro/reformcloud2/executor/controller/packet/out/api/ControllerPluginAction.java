package de.klaro.reformcloud2.executor.controller.packet.out.api;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;
import de.klaro.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;

public final class ControllerPluginAction extends DefaultPacket {

    public ControllerPluginAction(Action action, DefaultInstallablePlugin plugin) {
        super(47, new JsonConfiguration().add("action", action).add("installable", plugin));
    }

    public ControllerPluginAction(Action action, DefaultPlugin plugin) {
        super(47, new JsonConfiguration().add("action", action).add("plugin", plugin));
    }

    public enum Action {

        INSTALL,

        UNINSTALL
    }
}
