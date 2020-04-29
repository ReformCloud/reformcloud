package systems.reformcloud.reformcloud2.executor.controller.api.console;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.source.MemoryCachedCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;

import java.util.ArrayList;
import java.util.Collection;

public class ConsoleAPIImplementation implements ConsoleSyncAPI, ConsoleAsyncAPI {

    public ConsoleAPIImplementation(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.console = new ConsoleCommandSource(commandManager);
    }

    private final CommandManager commandManager;

    private final CommandSource console;

    @NotNull
    @Override
    public Task<String> dispatchCommandAndGetResultAsync(@NotNull String commandLine) {
        Task<String> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.commandManager.dispatchCommand(console, AllowedCommandSources.ALL, commandLine, s -> {
            });
            task.complete("Success");
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Collection<String>> dispatchConsoleCommandAndGetResultAsync(@NotNull String commandLine) {
        return Task.supply(() -> {
            Collection<String> result = new ArrayList<>();
            CommandSource source = new MemoryCachedCommandSource(result, ControllerExecutor.getInstance().getCommandManager());

            ControllerExecutor.getInstance().getCommandManager().dispatchCommand(
                    source,
                    AllowedCommandSources.ALL,
                    commandLine,
                    message -> result.add(message)
            );
            return result;
        });
    }

    @Override
    public String dispatchCommandAndGetResult(@NotNull String commandLine) {
        return dispatchCommandAndGetResultAsync(commandLine).getUninterruptedly();
    }

    @NotNull
    @Override
    public Collection<String> dispatchConsoleCommandAndGetResult(@NotNull String commandLine) {
        Collection<String> result = this.dispatchConsoleCommandAndGetResultAsync(commandLine).getUninterruptedly();
        return result == null ? new ArrayList<>() : result;
    }

    public final CommandSource getConsole() {
        return console;
    }
}
