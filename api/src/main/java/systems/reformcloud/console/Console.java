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
package systems.reformcloud.console;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.task.Task;

/**
 * Represents the cli of the system.
 */
public interface Console extends AutoCloseable {

  /**
   * Waits for the next input and completes the returned future.
   *
   * @return A future completed on the next cli input.
   */
  @NotNull
  Task<String> readString();

  /**
   * The current prompt of this console.
   *
   * @return The current prompt of this console.
   */
  @NotNull
  String getPrompt();

  /**
   * Sets the current prompt of this console.
   *
   * @param prompt The new prompt of this console.
   */
  void setPrompt(@NotNull String prompt);

  /**
   * Adds the specific {@code entry} to the history.
   *
   * @param entry The entry to add to the history.
   */
  void addHistoryEntry(@NotNull String entry);

  /**
   * Clears the history.
   */
  void clearHistory();

  /**
   * Clears this console.
   */
  void clearScreen();
}
