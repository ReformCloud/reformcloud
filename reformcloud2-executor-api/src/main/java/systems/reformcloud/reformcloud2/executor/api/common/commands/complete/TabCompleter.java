package systems.reformcloud.reformcloud2.executor.api.common.commands.complete;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

import java.util.Arrays;
import java.util.Collection;

/**
 * Represents any command which can be tab completed
 */
public interface TabCompleter {

    /**
     * Completes a command
     *
     * @param commandSource The command source of the command
     * @param commandLine   The command line with was given by the user
     * @param currentArg    The current arguments
     * @return The completed command arguments
     */
    @NotNull
    Collection<String> complete(@NotNull CommandSource commandSource, @NotNull String commandLine, @NotNull String[] currentArg);

    /**
     * Creates a collection of strings of an array
     *
     * @param strings The array which should get converted
     * @return The created collection of the given array
     */
    @NotNull
    default Collection<String> convert(@NotNull String... strings) {
        return Arrays.asList(strings);
    }
}
