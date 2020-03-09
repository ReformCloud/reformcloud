package systems.reformcloud.reformcloud2.executor.api.common.api.console;

import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public interface ConsoleAsyncAPI {

    /**
     * Sends a coloured line into the console
     *
     * @param line The line which should be sent
     * @return A task which will be completed if the action is completed (always {@code null})
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> sendColouredLineAsync(@Nonnull String line);

    /**
     * Sends a raw line into the console
     *
     * @param line The line which should be sent
     * @return A task which will be completed if the action is completed (always {@code null})
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> sendRawLineAsync(@Nonnull String line);

    /**
     * Dispatches a command into the console and waits for a result
     *
     * @param commandLine The command line which should be executed
     * @return A task which will be completed with the result of the command or {@code null} if the command is
     * a) not registered
     * b) doesn't sent any result to the handler
     */
    @Nonnull
    @CheckReturnValue
    Task<String> dispatchCommandAndGetResultAsync(@Nonnull String commandLine);

    /**
     * Gets a command which is registered
     *
     * @param name The name of the command
     * @return A task which will be completed with the command or {@code null} if the command is not registered
     */
    @Nonnull
    @CheckReturnValue
    Task<Command> getCommandAsync(@Nonnull String name);

    /**
     * Checks if a specific command is registered
     *
     * @param name The name of the command
     * @return A task which will be completed with {@code true} if the command is registered or else {@code false}
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> isCommandRegisteredAsync(@Nonnull String name);
}
