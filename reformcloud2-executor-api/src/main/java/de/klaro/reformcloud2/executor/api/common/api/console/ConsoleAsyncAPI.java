package de.klaro.reformcloud2.executor.api.common.api.console;

import de.klaro.reformcloud2.executor.api.common.commands.Command;
import de.klaro.reformcloud2.executor.api.common.utility.task.Task;

public interface ConsoleAsyncAPI extends ConsoleSyncAPI {

    Task<Void> sendColouredLineAsync(String line) throws IllegalAccessException;

    Task<Void> sendRawLineAsync(String line);

    Task<String> dispatchCommandAndGetResultAsync(String commandLine);

    Task<Command> getControllerCommandAsync(String name);

    /**
     * @deprecated This method will not work outside of controller
     */
    @Deprecated
    Task<Void> registerControllerCommandAsync(Command command);

    Task<Boolean> isControllerCommandRegisteredAsync(String name);
}
