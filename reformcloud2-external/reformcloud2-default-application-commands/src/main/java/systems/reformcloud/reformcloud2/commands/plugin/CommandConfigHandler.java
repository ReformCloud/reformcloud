package systems.reformcloud.reformcloud2.commands.plugin;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;

public abstract class CommandConfigHandler {

  private static CommandConfigHandler instance;

  public static CommandConfigHandler getInstance() { return instance; }

  public static void setInstance(@Nonnull CommandConfigHandler handler) {
    Conditions.isTrue(instance == null);
    instance = handler;
  }

  public abstract void
  handleCommandConfigRelease(@Nonnull CommandsConfig commandsConfig);

  public abstract void unregisterAllCommands();
}
