package systems.reformcloud.reformcloud2.executor.api.common.commands;

/**
 * Represents the allowed sources of a command
 *
 * @see Command#sources()
 */
public enum AllowedCommandSources {

  /**
   * The command is only available from the network
   */
  NETWORK,

  /**
   * The command is only available from the console
   */
  CONSOLE,

  /**
   * The command is only available from the api
   */
  API,

  /**
   * The command is only available from the rest-api
   */
  REST,

  /**
   * All command sources are allowed
   */
  ALL
}
