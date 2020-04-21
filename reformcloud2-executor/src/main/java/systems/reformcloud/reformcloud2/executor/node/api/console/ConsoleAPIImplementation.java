package systems.reformcloud.reformcloud2.executor.node.api.console;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.source.MemoryCachedCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

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
    public Task<Void> sendColouredLineAsync(@NotNull String line) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            System.out.println(line);
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> sendRawLineAsync(@NotNull String line) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            System.out.println(line);
            task.complete(null);
        });
        return task;
    }

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
            CommandSource source = new MemoryCachedCommandSource(result, NodeExecutor.getInstance().getCommandManager());

            NodeExecutor.getInstance().getCommandManager().dispatchCommand(
                    source,
                    AllowedCommandSources.ALL,
                    commandLine,
                    message -> result.add(message)
            );
            return result;
        });
    }

    @NotNull
    @Override
    public Task<Command> getCommandAsync(@NotNull String name) {
        Task<Command> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(commandManager.getCommand(name)));
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> isCommandRegisteredAsync(@NotNull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(commandManager.getCommand(name) != null));
        return task;
    }

    @Override
    public void sendColouredLine(@NotNull String line) {
        sendColouredLineAsync(line).awaitUninterruptedly();
    }

    @Override
    public void sendRawLine(@NotNull String line) {
        sendRawLineAsync(line).awaitUninterruptedly();
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

    @Override
    public Command getCommand(@NotNull String name) {
        return getCommandAsync(name).getUninterruptedly();
    }

    @Override
    public boolean isCommandRegistered(@NotNull String name) {
        Boolean result = isCommandRegisteredAsync(name).getUninterruptedly();
        return result == null ? false : result;
    }

    public final CommandSource getConsole() {
        return console;
    }
}
