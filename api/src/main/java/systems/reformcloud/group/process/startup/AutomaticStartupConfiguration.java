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
package systems.reformcloud.group.process.startup;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import systems.reformcloud.network.data.SerializableObject;

import java.util.concurrent.TimeUnit;

/**
 * A configuration for a group which handles the automatic starting and stopping of a group.
 */
public interface AutomaticStartupConfiguration extends SerializableObject, Cloneable {

  /**
   * Creates a startup configuration with automatic start and stop enabled.
   *
   * @return A startup configuration with automatic start and stop enabled.
   */
  @NotNull
  @Contract(pure = true)
  static AutomaticStartupConfiguration enabled() {
    return new DefaultAutomaticStartupConfiguration(true, true);
  }

  /**
   * Creates a startup configuration with automatic start and stop disabled.
   *
   * @return A startup configuration with automatic start and stop disabled.
   */
  @NotNull
  @Contract(pure = true)
  static AutomaticStartupConfiguration disabled() {
    return new DefaultAutomaticStartupConfiguration(false, false);
  }

  /**
   * Creates a startup configuration with automatic start enabled and automatic stop disabled.
   *
   * @return A startup configuration with automatic start enabled and automatic stop disabled.
   */
  @NotNull
  @Contract(pure = true)
  static AutomaticStartupConfiguration onlyAutomaticStartup() {
    return new DefaultAutomaticStartupConfiguration(true, false);
  }

  /**
   * Creates a startup configuration with automatic start disabled and automatic stop enabled.
   *
   * @return A startup configuration with automatic start disabled and automatic stop enabled.
   */
  @NotNull
  @Contract(pure = true)
  static AutomaticStartupConfiguration onlyAutomaticShutdown() {
    return new DefaultAutomaticStartupConfiguration(false, true);
  }

  /**
   * Get if the automatic startup of the group is enabled.
   *
   * @return If the automatic startup of the group is enabled.
   */
  boolean isAutomaticStartupEnabled();

  /**
   * Set the new state of the automatic startup of the group.
   *
   * @param automaticStartupEnabled the new state of the automatic startup of the group.
   */
  void setAutomaticStartupEnabled(boolean automaticStartupEnabled);

  /**
   * Get if the automatic shutdown of the group is enabled.
   *
   * @return If the automatic shutdown of a group is enabled.
   */
  boolean isAutomaticShutdownEnabled();

  /**
   * Sets if the automatic shutdown of the group is enabled.
   *
   * @param automaticShutdownEnabled If the automatic shutdown of the group is enabled.
   */
  void setAutomaticShutdownEnabled(boolean automaticShutdownEnabled);

  /**
   * Get the max percentage of player to start a new process of a group.
   *
   * @return The percentage of players to start a new process of a group.
   */
  @Range(from = 0, to = 100)
  int getMaxPercentOfPlayersToStart();

  /**
   * Sets the max percentage of players before a new process is started.
   *
   * @param maxPercentOfPlayersToStart the max percentage of players before a new process is started.
   */
  void setMaxPercentOfPlayersToStart(@Range(from = 0, to = 100) int maxPercentOfPlayersToStart);

  /**
   * Get the max percentage of players before a server gets automatically stopped.
   *
   * @return the max percentage of players before a server gets automatically stopped.
   */
  @Range(from = 0, to = 100)
  int getMaxPercentOfPlayersToStop();

  /**
   * Sets the max percentage of players before a server gets automatically stopped.
   *
   * @param maxPercentOfPlayersToStop the max percentage of players before a server gets automatically stopped.
   */
  void setMaxPercentOfPlayersToStop(@Range(from = 0, to = 100) int maxPercentOfPlayersToStop);

  /**
   * Get the check interval in seconds between every start and stop check.
   *
   * @return the check interval in seconds between every start and stop check.
   */
  @Range(from = 0, to = Long.MAX_VALUE)
  long getCheckIntervalInSeconds();

  /**
   * Sets the check interval in seconds between every start and stop check.
   *
   * @param checkIntervalInSeconds the check interval in seconds between every start and stop check.
   */
  void setCheckIntervalInSeconds(@Range(from = 0, to = Long.MAX_VALUE) long checkIntervalInSeconds);

  /**
   * Sets the check interval in seconds between every start and stop check.
   *
   * @param timeUnit      The time unit of the provided {@code checkInterval}.
   * @param checkInterval The check interval converted to seconds using the given {@code timeUnit}.
   */
  void setCheckInterval(@NotNull TimeUnit timeUnit, @Range(from = 0, to = Long.MAX_VALUE) long checkInterval);

  /**
   * Creates a clone of this configuration.
   *
   * @return A clone of this configuration.
   */
  @NotNull
  AutomaticStartupConfiguration clone();
}
