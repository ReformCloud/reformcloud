/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommandManager {

  @NotNull
  default CommandManager registerCommand(@NotNull Command command, @NotNull String description, @NotNull String... aliases) {
    return this.registerCommand(command, description, Arrays.asList(aliases));
  }

  @NotNull
  CommandManager registerCommand(@NotNull Command command, @NotNull String description, @NotNull List<String> aliases);

  void unregisterCommand(@NotNull CommandContainer command);

  void unregisterCommand(@NotNull String... aliases);

  @NotNull
  Optional<CommandContainer> getCommand(@NotNull String anyAlias);

  @NotNull
  @UnmodifiableView Collection<CommandContainer> getCommands();

  boolean process(@NotNull String commandLine, @NotNull CommandSender commandSender);

  @NotNull
  List<String> suggest(@NotNull String commandLine, @NotNull CommandSender commandSender);
}
