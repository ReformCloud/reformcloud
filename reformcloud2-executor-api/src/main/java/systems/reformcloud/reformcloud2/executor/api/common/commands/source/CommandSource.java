package systems.reformcloud.reformcloud2.executor.api.common.commands.source;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.PermissionHolder;

public interface CommandSource extends PermissionHolder {

  /**
   * Sends a message to the source of the command
   *
   * @param message The message which should be sent
   */
  void sendMessage(@Nonnull String message);

  /**
   * Sends a raw message to the source of the command
   *
   * @param message The message which should be sent
   */
  void sendRawMessage(@Nonnull String message);

  /**
   * Sends many messages to the source of the command
   *
   * @param messages The messages which should be sent
   */
  void sendMessages(@Nonnull String[] messages);

  /**
   * Sends many messages to the source of the command
   *
   * @param messages The messages which should be sent
   */
  void sendRawMessages(@Nonnull String[] messages);

  /**
   * @return The command manger of the source
   */
  @Nonnull CommandManager commandManager();
}
