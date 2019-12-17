package systems.reformcloud.reformcloud2.executor.node.network.packet.out.api;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultInstallablePlugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;

public final class NodePluginAction extends DefaultPacket {

  public NodePluginAction(Action action, String process,
                          DefaultInstallablePlugin plugin) {
    super(47, new JsonConfiguration()
                  .add("action", action)
                  .add("process", process)
                  .add("installable", plugin));
  }

  public NodePluginAction(Action action, String process, DefaultPlugin plugin) {
    super(47, new JsonConfiguration()
                  .add("action", action)
                  .add("process", process)
                  .add("plugin", plugin));
  }

  public enum Action {

    INSTALL,

    UNINSTALL
  }
}
