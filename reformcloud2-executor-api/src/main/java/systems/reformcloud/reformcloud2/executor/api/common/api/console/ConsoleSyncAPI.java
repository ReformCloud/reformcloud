package systems.reformcloud.reformcloud2.executor.api.common.api.console;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;

public interface ConsoleSyncAPI {

  /**
   * Sends a coloured line into the console
   *
   * @param line The line which should be sent
   * @throws IllegalAccessException If coloured logging is not supported
   */
  void sendColouredLine(@Nonnull String line) throws IllegalAccessException;

  /**
   * Sends a raw line into the console
   *
   * @param line The line which should be sent
   */
  void sendRawLine(@Nonnull String line);

  /**
   * Dispatches a command into the console and waits for a result
   *
   * @param commandLine The command line which should be executed
   * @return The result of the command or {@code null} if the command is
   *  a) not registered
   *  b) doesn't sent any result to the handler
   */
  @Nullable String dispatchCommandAndGetResult(@Nonnull String commandLine);

  /**
   * Gets a command which is registered
   *
   * @param name The name of the command
   * @return The command or {@code null} if the command is not registered
   */
  @Nullable Command getCommand(@Nonnull String name);

  /**
   * Checks if a specific command is registered
   *
   * @param name The name of the command
   * @return {@code true} if the command is registered or else {@code false}
   */
  boolean isCommandRegistered(@Nonnull String name);
}
