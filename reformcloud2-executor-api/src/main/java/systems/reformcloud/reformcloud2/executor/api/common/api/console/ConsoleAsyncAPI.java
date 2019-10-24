package systems.reformcloud.reformcloud2.executor.api.common.api.console;

import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

public interface ConsoleAsyncAPI extends ConsoleSyncAPI {

    /**
     * Sends a coloured line into the console
     *
     * @param line The line which should be sent
     * @return A task which will be completed if the action is completed (always {@code null})
     * @throws IllegalAccessException If coloured logging is not supported
     */
    Task<Void> sendColouredLineAsync(String line) throws IllegalAccessException;

    /**
     * Sends a raw line into the console
     *
     * @param line The line which should be sent
     * @return A task which will be completed if the action is completed (always {@code null})
     */
    Task<Void> sendRawLineAsync(String line);

    /**
     * Dispatches a command into the console and waits for a result
     *
     * @param commandLine The command line which should be executed
     * @return A task which will be completed with the result of the command or {@code null} if the command is
     *  a) not registered
     *  b) doesn't sent any result to the handler
     */
    Task<String> dispatchCommandAndGetResultAsync(String commandLine);

    /**
     * Gets a command which is registered
     *
     * @param name The name of the command
     * @return A task which will be completed with the command or {@code null} if the command is not registered
     */
    Task<Command> getCommandAsync(String name);

    /**
     * Checks if a specific command is registered
     *
     * @param name The name of the command
     * @return A task which will be completed with {@code true} if the command is registered or else {@code false}
     */
    Task<Boolean> isCommandRegisteredAsync(String name);
}
