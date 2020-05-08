package systems.reformcloud.reformcloud2.executor.api.common.api.console;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.Collection;

public interface ConsoleAsyncAPI {

    /**
     * Dispatches a command into the console and waits for a result
     *
     * @param commandLine The command line which should be executed
     * @return A task which will be completed with the result of the command or {@code null} if the command is
     * a) not registered
     * b) doesn't sent any result to the handler
     * @deprecated Use {@link #dispatchConsoleCommandAndGetResultAsync(String)} instead
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5")
    @NotNull
    Task<String> dispatchCommandAndGetResultAsync(@NotNull String commandLine);

    /**
     * Executes a command in the controller/node console
     *
     * @param commandLine The command line which should be executed
     * @return A task which will be completed with the full result of messages sent by the console command
     */
    @NotNull
    Task<Collection<String>> dispatchConsoleCommandAndGetResultAsync(@NotNull String commandLine);
}
