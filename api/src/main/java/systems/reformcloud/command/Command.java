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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an executable command callback registered in a {@link CommandManager} instance.
 */
public interface Command {

  /**
   * Called when this command is processed.
   *
   * @param sender      The sender of the command.
   * @param strings     The arguments provided by the sender.
   * @param commandLine The full command line sent.
   */
  void process(@NotNull CommandSender sender, @NonNls String[] strings, @NotNull String commandLine);

  /**
   * Checks if the given {@code commandSender} can execute this command
   *
   * @param commandSender The command sender trying to access the command.
   * @return {@code true} if the command sender can use the command, else {@code false}.
   */
  default boolean canAccess(@NotNull CommandSender commandSender) {
    return true;
  }

  /**
   * Gives suggestions for the command arguments in dependence of the currently given
   * {@code strings} arguments and the current {@code bufferIndex}.
   *
   * @param commandSender The sender who requested the suggestions.
   * @param strings       The arguments which are already provided in the command line.
   * @param bufferIndex   The current buffer index of the typing.
   * @param commandLine   The full command line.
   * @return The suggestions for the current {@code strings} arguments.
   */
  @NotNull
  default List<String> suggest(@NotNull CommandSender commandSender, @NonNls String[] strings, int bufferIndex, @NotNull String commandLine) {
    return new ArrayList<>();
  }
}
