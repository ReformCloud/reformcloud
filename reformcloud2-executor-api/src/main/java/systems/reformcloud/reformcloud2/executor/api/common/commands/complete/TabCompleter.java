package systems.reformcloud.reformcloud2.executor.api.common.commands.complete;

import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;

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
    Collection<String> complete(@Nonnull CommandSource commandSource, @Nonnull String commandLine, @Nonnull String[] currentArg);

    @Nonnull
    default Collection<String> convert(@Nonnull String... strings) {
        return Arrays.asList(strings);
    }
}
