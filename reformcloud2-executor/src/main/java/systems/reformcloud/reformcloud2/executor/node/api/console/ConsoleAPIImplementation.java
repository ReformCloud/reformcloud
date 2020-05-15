/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.node.api.console;

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
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.ArrayList;
import java.util.Collection;

public class ConsoleAPIImplementation implements ConsoleSyncAPI, ConsoleAsyncAPI {

    private final CommandManager commandManager;
    private final CommandSource console;

    public ConsoleAPIImplementation(CommandManager commandManager) {
        this.commandManager = commandManager;
        this.console = new ConsoleCommandSource(commandManager);
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
