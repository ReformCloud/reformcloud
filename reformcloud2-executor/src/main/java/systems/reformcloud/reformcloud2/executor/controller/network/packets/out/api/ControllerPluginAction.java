package systems.reformcloud.reformcloud2.executor.controller.network.packets.out.api;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;

public final class ControllerPluginAction extends JsonPacket {

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
