package systems.reformcloud.reformcloud2.executor.api.common.commands.complete;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

/**
 * Represents any command which can be tab completed
 *
 * @deprecated This feature is not implemented yet
 */
@Deprecated
public interface TabCompleter {

  /**
   * Completes a command
   *
   * @param commandSource The command source of the command
   * @param commandLine The command line with was given by the user
   * @param currentArg The current arguments
   * @return The completed command arguments
   */
  @Nonnull
  Collection<String> complete(@Nonnull CommandSource commandSource,
                              @Nonnull String commandLine,
                              @Nonnull String[] currentArg);

  /**
   * Creates a collection of strings of an array
   *
   * @param strings The array which should get converted
   * @return The created collection of the given array
   */
  @Nonnull
  default Collection<String> convert(@Nonnull String... strings) {
    return Arrays.asList(strings);
  }
}
