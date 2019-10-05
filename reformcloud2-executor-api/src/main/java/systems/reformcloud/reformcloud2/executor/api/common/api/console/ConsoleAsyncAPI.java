package systems.reformcloud.reformcloud2.executor.api.common.api.console;

import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

public interface ConsoleAsyncAPI extends ConsoleSyncAPI {

    Task<Void> sendColouredLineAsync(String line) throws IllegalAccessException;

    Task<Void> sendRawLineAsync(String line);

    Task<String> dispatchCommandAndGetResultAsync(String commandLine);

    Task<Command> getControllerCommandAsync(String name);

    Task<Boolean> isControllerCommandRegisteredAsync(String name);
}
