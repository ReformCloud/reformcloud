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
package systems.reformcloud.reformcloud2.shared.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.command.Command;
import systems.reformcloud.reformcloud2.executor.api.command.CommandContainer;
import systems.reformcloud.reformcloud2.executor.api.command.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.command.CommandSender;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultCommandManager implements CommandManager {

    private final Collection<CommandContainer> commands = new CopyOnWriteArrayList<>();

    @Override
    public @NotNull CommandManager registerCommand(@NotNull Command command, @NotNull String description, @NotNull List<String> aliases) {
        for (String alias : aliases) {
            Optional<CommandContainer> registeredCommand = this.getCommand(alias.toLowerCase());
            if (registeredCommand.isPresent()) {
                throw new RuntimeException("Command " + registeredCommand.get().getCommand().getClass().getName() + " clashes with "
                        + command.getClass().getName() + " because of alias '" + alias + "'");
            }
        }

        this.commands.add(new DefaultCommandContainer(aliases, description, command));
        return this;
    }

    @Override
    public void unregisterCommand(@NotNull CommandContainer command) {
        this.commands.removeIf(commandContainer -> {
            for (String alias : commandContainer.getAliases()) {
                if (command.getAliases().stream().anyMatch(alias::equals)) {
                    return true;
                }
            }

            return false;
        });
    }

    @Override
    public void unregisterCommand(@NotNull String... aliases) {
        Collection<String> toUnregister = Streams.toLowerCase(Arrays.asList(aliases));
        this.commands.removeIf(command -> Streams.hasMatch(command.getAliases(), toUnregister::contains));
    }

    @NotNull
    @Override
    public Optional<CommandContainer> getCommand(@NotNull String anyAlias) {
        for (CommandContainer command : this.commands) {
            if (command.getAliases().contains(anyAlias.toLowerCase())) {
                return Optional.of(command);
            }
        }

        return Optional.empty();
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<CommandContainer> getCommands() {
        return Collections.unmodifiableCollection(this.commands);
    }

    @Override
    public boolean process(@NotNull String commandLine, @NotNull CommandSender commandSender) {
        String[] split = commandLine.split(" ");
        CommandContainer command = this.getCommand(commandSender, split);
        if (command == null) {
            return false;
        }

        String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[0];
        command.getCommand().process(commandSender, args, commandLine);
        return true;
    }

    @NotNull
    @Override
    public List<String> suggest(@NotNull String commandLine, @NotNull CommandSender commandSender) {
        String[] split = commandLine.split(" ");
        CommandContainer command = this.getCommand(commandSender, split);
        if (command == null) {
            return new ArrayList<>();
        }

        String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[0];
        return command.getCommand().suggest(commandSender, args, commandLine);
    }

    private @Nullable CommandContainer getCommand(@NotNull CommandSender commandSender, @NotNull String[] split) {
        if (split.length == 0) {
            return null;
        }

        Optional<CommandContainer> command = this.getCommand(split[0]);
        return command.filter(commandContainer -> commandContainer.getCommand().canAccess(commandSender)).orElse(null);
    }
}
