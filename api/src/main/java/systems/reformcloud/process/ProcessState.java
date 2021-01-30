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

/**
 * The process states.
 */
public enum ProcessState {
  /**
   * The process information was just created.
   */
  CREATED,
  /**
   * The process is prepared and ready to start.
   */
  PREPARED,
  /**
   * The process is started.
   */
  STARTED,
  /**
   * The process is started and connected to a node.
   */
  READY,
  /**
   * The process is started and connected to a node but exceeding the player limit.
   */
  FULL,
  /**
   * The process is started and connected to a node but invisible.
   */
  INVISIBLE,
  /**
   * The process is currently restarting.
   */
  RESTARTING,
  /**
   * The process is stopped but ready to get started any time.
   */
  PAUSED,
  /**
   * The process is stopped and will be deleted soon.
   */
  STOPPED;

  /**
   * Gets if the state started or online.
   *
   * @return If the state started or online.
   */
  public boolean isStartedOrOnline() {
    return this == STARTED || this.isOnline();
  }

  /**
   * Gets if the state is online.
   *
   * @return If the state is online.
   */
  public boolean isOnline() {
    return this == READY || this == FULL || this == INVISIBLE;
  }

  /**
   * Gets if the state is a runtime state.
   *
   * @return If the state is a runtime state.
   */
  public boolean isRuntimeState() {
    return this == STARTED || this == RESTARTING || this == PAUSED || this == STOPPED;
  }
}
