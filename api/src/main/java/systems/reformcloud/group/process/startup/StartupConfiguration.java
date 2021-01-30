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

import java.util.Collection;

/**
 * A startup configuration managing the automatic startup of processes for a group.
 */
public interface StartupConfiguration extends SerializableObject, Cloneable {

  /**
   * Creates a new default configuration with
   * - the maximum process amount set to {@code -1} (disabled),
   * - the always started process amount to {@code 1},
   * - the always prepared (not started but ready to start) process amount to {@code 1}.
   *
   * @return A new default configuration.
   */
  @NotNull
  @Contract(pure = true)
  static StartupConfiguration newDefaultConfiguration() {
    return new DefaultStartupConfiguration(-1, 1, 1);
  }

  /**
   * Creates a new startup configuration.
   *
   * @param maximumProcessAmount        The maximum started process amount.
   * @param alwaysOnlineProcessAmount   The minimum started process amount.
   * @param alwaysPreparedProcessAmount The always prepared (not started but ready to start) process amount.
   * @return The created startup configuration.
   */
  @NotNull
  @Contract(value = "_, _, _ -> new", pure = true)
  static StartupConfiguration configuration(int maximumProcessAmount, int alwaysOnlineProcessAmount, int alwaysPreparedProcessAmount) {
    return new DefaultStartupConfiguration(maximumProcessAmount, alwaysOnlineProcessAmount, alwaysPreparedProcessAmount);
  }

  /**
   * Creates a new startup configuration.
   *
   * @param maximumProcessAmount        The maximum started process amount.
   * @param alwaysOnlineProcessAmount   The minimum started process amount.
   * @param alwaysPreparedProcessAmount The always prepared (not started but ready to start) process amount.
   * @param jvmCommand                  The jvm command used when starting new processes based on the configuration.
   * @param startupConfiguration        The configuration handling the automatic startup and shutdown of servers/proxies.
   * @param startingNodes               The nodes which are allowed to start a process of the group.
   * @return The created startup configuration.
   */
  @NotNull
  @Contract(value = "_, _, _, _, _, _ -> new", pure = true)
  static StartupConfiguration configuration(int maximumProcessAmount, int alwaysOnlineProcessAmount, int alwaysPreparedProcessAmount,
                                            @NotNull String jvmCommand, @NotNull AutomaticStartupConfiguration startupConfiguration, @NotNull Collection<String> startingNodes) {
    return new DefaultStartupConfiguration(maximumProcessAmount, alwaysOnlineProcessAmount, alwaysPreparedProcessAmount, jvmCommand, startupConfiguration, startingNodes);
  }

  /**
   * Get the maximum process amount of the associated group.
   *
   * @return The maximum process amount of the associated group.
   */
  @Range(from = -1, to = Integer.MAX_VALUE)
  int getMaximumProcessAmount();

  /**
   * Sets the maximum process amount of the associated group.
   *
   * @param maximumProcessAmount the maximum process amount of the associated group.
   */
  void setMaximumProcessAmount(@Range(from = -1, to = Integer.MAX_VALUE) int maximumProcessAmount);

  /**
   * Get the always online process amount. (Amount of processes which are always started)
   *
   * @return the always online process amount.
   */
  @Range(from = 0, to = Integer.MAX_VALUE)
  int getAlwaysOnlineProcessAmount();

  /**
   * Sets the always online process amount. (Amount of processes which are always started)
   *
   * @param alwaysOnlineProcessAmount the always online process amount.
   */
  void setAlwaysOnlineProcessAmount(@Range(from = 0, to = Integer.MAX_VALUE) int alwaysOnlineProcessAmount);

  /**
   * Get the always prepared process amount. (Amount of processes which are always prepared but not started)
   *
   * @return the always prepared process amount.
   */
  @Range(from = 0, to = Integer.MAX_VALUE)
  int getAlwaysPreparedProcessAmount();

  /**
   * Sets the always prepared process amount. (Amount of processes which are always prepared but not started)
   *
   * @param alwaysPreparedProcessAmount the always prepared process amount.
   */
  void setAlwaysPreparedProcessAmount(@Range(from = 0, to = Integer.MAX_VALUE) int alwaysPreparedProcessAmount);

  /**
   * Get the jvm command for starting the process associated with this config.
   *
   * @return the jvm command for starting the process associated with this config.
   */
  @NotNull
  String getJvmCommand();

  /**
   * Sets the jvm command for starting the process associated with this config.
   *
   * @param jvmCommand the jvm command for starting the process associated with this config.
   */
  void setJvmCommand(@NotNull String jvmCommand);

  /**
   * Get the automatic startup configuration.
   *
   * @return the automatic startup configuration.
   */
  @NotNull
  AutomaticStartupConfiguration getAutomaticStartupConfiguration();

  /**
   * Sets the automatic startup configuration.
   *
   * @param automaticStartupConfiguration the automatic startup configuration.
   */
  void setAutomaticStartupConfiguration(@NotNull AutomaticStartupConfiguration automaticStartupConfiguration);

  /**
   * Get the nodes which are allowed to start a process of this group.
   *
   * @return the nodes which are allowed to start a process of this group.
   */
  @NotNull
  Collection<String> getStartingNodes();

  /**
   * Sets the nodes which are allowed to start a process of this group.
   *
   * @param startingNodes the nodes which are allowed to start a process of this group.
   */
  void setStartingNodes(@NotNull Collection<String> startingNodes);

  /**
   * Adds a node which is allowed to start a process of this group.
   *
   * @param node the name of the node which is allowed to start a process of this group.
   */
  void addStartingNode(@NotNull String node);

  /**
   * Removes a node which is allowed to start a process of this group.
   *
   * @param node the name of the node which is no longer allowed to start a process of this group.
   */
  void removeStartingNode(@NotNull String node);

  /**
   * Creates a clone of this configuration.
   *
   * @return A clone of this configuration.
   */
  @NotNull
  StartupConfiguration clone();
}
