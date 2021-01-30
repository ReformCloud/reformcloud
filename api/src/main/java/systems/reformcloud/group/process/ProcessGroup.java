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
package systems.reformcloud.group.process;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.builder.ProcessGroupBuilder;
import systems.reformcloud.configuration.data.JsonDataHolder;
import systems.reformcloud.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.group.process.startup.StartupConfiguration;
import systems.reformcloud.group.template.TemplateHolder;
import systems.reformcloud.network.data.SerializableObject;
import systems.reformcloud.process.builder.ProcessBuilder;
import systems.reformcloud.utility.name.Nameable;

import java.util.Optional;

/**
 * A group from which processes are created.
 */
public interface ProcessGroup extends Nameable, TemplateHolder, JsonDataHolder<ProcessGroup>, SerializableObject, Cloneable {

  /**
   * Creates a new builder for a group.
   *
   * @param name The name of the group.
   * @return A new builder for a group.
   */
  @NotNull
  @Contract(value = "_ -> new", pure = true)
  static ProcessGroupBuilder builder(@NotNull String name) {
    return ExecutorAPI.getInstance().getProcessGroupProvider().createProcessGroup(name);
  }

  /**
   * Get an existing process group by it's name.
   *
   * @param name The name of the group to get.
   * @return The group.
   */
  @NotNull
  static Optional<ProcessGroup> getByName(@NotNull String name) {
    return ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(name);
  }

  /**
   * Get the startup configuration of this group.
   *
   * @return The startup configuration of this group.
   */
  @NotNull
  StartupConfiguration getStartupConfiguration();

  /**
   * Sets the startup configuration of this group.
   *
   * @param startupConfiguration The startup configuration to use.
   */
  void setStartupConfiguration(@NotNull StartupConfiguration startupConfiguration);

  /**
   * Gets the player access configuration of this group.
   *
   * @return the player access configuration of this group.
   */
  @NotNull
  PlayerAccessConfiguration getPlayerAccessConfiguration();

  /**
   * Sets the player access configuration for this group.
   *
   * @param playerAccessConfiguration the player access configuration for this group.
   */
  void setPlayerAccessConfiguration(@NotNull PlayerAccessConfiguration playerAccessConfiguration);

  /**
   * Get if the id is visible in the display name of a process created from this group.
   *
   * @return if the id is visible in the display name of a process created from this group.
   */
  boolean showIdInName();

  /**
   * Sets if the id is visible in the display name of a process created from this group.
   *
   * @param showIdInName if the id is visible in the display name of a process created from this group.
   */
  void setShowIdInName(boolean showIdInName);

  /**
   * Get if the processes created by this group are static or temporary.
   *
   * @return if the processes created by this group are static or temporary.
   */
  boolean createsStaticProcesses();

  /**
   * Sets if the processes created by this group are static or temporary.
   *
   * @param createsStaticProcesses if the processes created by this group are static or temporary.
   */
  void setCreatesStaticProcesses(boolean createsStaticProcesses);

  /**
   * Get if the current group can be used as a lobby group. This has no effect on proxies.
   *
   * @return if the current group can be used as a lobby group.
   */
  boolean isLobbyGroup();

  /**
   * Sets if the current group can be used as a lobby group. This has no effect on proxies.
   *
   * @param lobbyGroup if the current group can be used as a lobby group.
   */
  void setLobbyGroup(boolean lobbyGroup);

  /**
   * Creates a new process builder based on this group.
   *
   * @return A process builder for a process based on this group.
   */
  @NotNull
  ProcessBuilder newProcess();

  /**
   * Publishes an update of this process group to the network. This is needed to synchronize changes
   * to all other nodes and processes in the network.
   */
  void update();

  /**
   * Creates a clone of this group.
   *
   * @return a clone of this group.
   */
  @NotNull
  ProcessGroup clone();
}
