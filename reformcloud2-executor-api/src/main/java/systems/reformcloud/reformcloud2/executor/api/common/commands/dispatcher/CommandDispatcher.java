package systems.reformcloud.reformcloud2.executor.api.common.commands.dispatcher;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.dispatcher.command.EventDispatcher;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

import java.util.function.Consumer;

public interface CommandDispatcher extends EventDispatcher {

    /**
     * Dispatches a command line
     *
     * @param commandSource  The command source where the command is from
     * @param commandSources The {@link AllowedCommandSources} of the command
     * @param commandLine    The command line which should be executed
     * @param result         The result handler of the method
     */
    void dispatchCommand(
            @NotNull CommandSource commandSource,
            @NotNull AllowedCommandSources commandSources,
            @NotNull String commandLine,
            @NotNull Consumer<String> result
    );
}
