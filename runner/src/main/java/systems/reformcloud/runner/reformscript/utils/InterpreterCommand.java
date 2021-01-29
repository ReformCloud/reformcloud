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
package systems.reformcloud.runner.reformscript.utils;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.runner.reformscript.InterpretedReformScript;

import java.util.Collection;

/**
 * Represents a command which can get executed from a reform script
 */
public abstract class InterpreterCommand {

  private final String command;

  /**
   * Creates new interpreter command
   *
   * @param command The name of the command
   */
  public InterpreterCommand(@NotNull String command) {
    this.command = command.toUpperCase();
  }

  /**
   * @return The actual command name which is always upper-case
   */
  @NotNull
  public String getCommand() {
    return this.command;
  }

  /**
   * Executes the current command
   *
   * @param cursorLine The current line of the cursor
   * @param script     The script from which the command got executed
   * @param allLines   All lines of the script
   */
  public abstract void execute(@NotNull String cursorLine, @NotNull InterpretedReformScript script, @NotNull Collection<String> allLines);
}
