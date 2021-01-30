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
package systems.reformcloud.process;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.data.SerializableObject;

/**
 * A holder for a single {@link ProcessState}.
 */
public interface ProcessStateHolder extends SerializableObject, Cloneable {

  /**
   * Gets the current process state of this holder.
   *
   * @return The current process state of this holder.
   */
  @NotNull
  ProcessState getCurrentState();

  /**
   * Sets the current process state of this holder.
   *
   * @param newState The current process state of this holder.
   */
  void setCurrentState(@NotNull ProcessState newState);

  /**
   * Gets the initial state of this holder.
   *
   * @return The initial state of this holder.
   */
  @NotNull
  ProcessState getInitialState();

  /**
   * Sets the initial state of this holder.
   *
   * @param newInitialState The initial state of this holder.
   */
  void setInitialState(@NotNull ProcessState newInitialState);

  /**
   * Creates a clone of this holder.
   *
   * @return A clone of this holder.
   */
  @NotNull
  ProcessStateHolder clone();
}
