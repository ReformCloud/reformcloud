package systems.reformcloud.reformcloud2.executor.controller.api.console;

import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.console.ConsoleSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.ConsoleCommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import javax.annotation.Nonnull;

public class ConsoleAPIImplementation implements ConsoleSyncAPI, ConsoleAsyncAPI {

    public ConsoleAPIImplementation(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.console = new ConsoleCommandSource(commandManager);
    }

    private final CommandManager commandManager;

    private final CommandSource console;

    @Nonnull
    @Override
    public Task<Void> sendColouredLineAsync(@Nonnull String line) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            System.out.println(line);
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> sendRawLineAsync(@Nonnull String line) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            System.out.println(line);
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<String> dispatchCommandAndGetResultAsync(@Nonnull String commandLine) {
        Task<String> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> this.commandManager.dispatchCommand(console, AllowedCommandSources.ALL, commandLine, task::complete));
        return task;
    }

    @Nonnull
    @Override
    public Task<Command> getCommandAsync(@Nonnull String name) {
        Task<Command> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(commandManager.getCommand(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> isCommandRegisteredAsync(@Nonnull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(commandManager.getCommand(name) != null));
        return task;
    }

    @Override
    public void sendColouredLine(@Nonnull String line) {
        sendColouredLineAsync(line).awaitUninterruptedly();
    }

    @Override
    public void sendRawLine(@Nonnull String line) {
        sendRawLineAsync(line).awaitUninterruptedly();
    }

    @Override
    public String dispatchCommandAndGetResult(@Nonnull String commandLine) {
        return dispatchCommandAndGetResultAsync(commandLine).getUninterruptedly();
    }

    @Override
    public Command getCommand(@Nonnull String name) {
        return getCommandAsync(name).getUninterruptedly();
    }

    @Override
    public boolean isCommandRegistered(@Nonnull String name) {
        return isCommandRegisteredAsync(name).getUninterruptedly();
    }

    public final CommandSource getConsole() {
        return console;
    }
}
