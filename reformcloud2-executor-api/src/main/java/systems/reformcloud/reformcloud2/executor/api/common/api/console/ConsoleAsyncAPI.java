package systems.reformcloud.reformcloud2.executor.api.common.api.console;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.Collection;

public interface ConsoleAsyncAPI {

    /**
     * Sends a coloured line into the console
     *
     * @param line The line which should be sent
     * @return A task which will be completed if the action is completed (always {@code null})
     */
    @NotNull
    Task<Void> sendColouredLineAsync(@NotNull String line);

    /**
     * Sends a raw line into the console
     *
     * @param line The line which should be sent
     * @return A task which will be completed if the action is completed (always {@code null})
     */
    @NotNull
    Task<Void> sendRawLineAsync(@NotNull String line);

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

    /**
     * Gets a command which is registered
     *
     * @param name The name of the command
     * @return A task which will be completed with the command or {@code null} if the command is not registered
     */
    @NotNull
    Task<Command> getCommandAsync(@NotNull String name);

    /**
     * Checks if a specific command is registered
     *
     * @param name The name of the command
     * @return A task which will be completed with {@code true} if the command is registered or else {@code false}
     */
    @NotNull
    Task<Boolean> isCommandRegisteredAsync(@NotNull String name);
}
