package systems.reformcloud.reformcloud2.executor.api.common.api.console;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ConsoleSyncAPI {

    /**
     * Dispatches a command into the console and waits for a result
     *
     * @param commandLine The command line which should be executed
     * @return The result of the command or {@code null} if the command is
     * a) not registered
     * b) doesn't sent any result to the handler
     * @deprecated Use {@link #dispatchConsoleCommandAndGetResult(String)} instead
     */
    @Nullable
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5")
    String dispatchCommandAndGetResult(@NotNull String commandLine);

    /**
     * Executes a command in the controller/node console
     *
     * @param commandLine The command line which should be executed
     * @return The full result of messages sent by the console command
     */
    @NotNull
    Collection<String> dispatchConsoleCommandAndGetResult(@NotNull String commandLine);
}
