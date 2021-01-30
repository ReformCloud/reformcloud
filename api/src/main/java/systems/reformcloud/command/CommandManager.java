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

/**
 * A manager for commands.
 */
public interface CommandManager {

  /**
   * Registers the specific command to this manager instance.
   *
   * @param command     The command to register.
   * @param description The description of the command to register.
   * @param aliases     The aliases of the command.
   * @return The same instance of the class, for chaining.
   * @see #registerCommand(Command, String, List)
   */
  @NotNull
  default CommandManager registerCommand(@NotNull Command command, @NotNull String description, @NotNull String... aliases) {
    return this.registerCommand(command, description, Arrays.asList(aliases));
  }

  /**
   * Registers the specific command to this manager instance.
   *
   * @param command     The command to register.
   * @param description The description of the command to register.
   * @param aliases     The aliases of the command.
   * @return The same instance of the class, for chaining.
   * @see #registerCommand(Command, String, String...)
   */
  @NotNull
  CommandManager registerCommand(@NotNull Command command, @NotNull String description, @NotNull List<String> aliases);

  /**
   * Unregisters the {@code command} from this command manager instance.
   *
   * @param command The command to unregister.
   * @see #unregisterCommand(String...)
   */
  void unregisterCommand(@NotNull CommandContainer command);

  /**
   * Unregisters the first command with one the provided {@code aliases}. The alias can
   * also be the name of the command.
   *
   * @param aliases The aliases of the command to unregister
   * @see #unregisterCommand(CommandContainer)
   */
  void unregisterCommand(@NotNull String... aliases);

  /**
   * Get a command known to this instance.
   *
   * @param anyAlias One alias of the command or the name of the command.
   * @return The command known to this instance.
   */
  @NotNull
  Optional<CommandContainer> getCommand(@NotNull String anyAlias);

  /**
   * Get all known commands.
   *
   * @return All known commands.
   */
  @NotNull
  @UnmodifiableView Collection<CommandContainer> getCommands();

  /**
   * Tries to find and process a {@link CommandContainer} in dependant to the provided
   * {@code commandLine} and {@code commandSender}.
   *
   * @param commandLine   The command line.
   * @param commandSender The sender who executed the command.
   * @return {@code true} if the command execution was successful, else {@code false}
   */
  boolean process(@NotNull String commandLine, @NotNull CommandSender commandSender);

  /**
   * Tries to find and suggest a {@link CommandContainer} in dependant to the provided
   * {@code commandLine} and {@code commandSender}.
   *
   * @param commandLine   The command line.
   * @param commandSender The sender who requested the suggestions.
   * @return The suggestions provided by the found command or an empty list.
   */
  @NotNull
  List<String> suggest(@NotNull String commandLine, @NotNull CommandSender commandSender);
}
