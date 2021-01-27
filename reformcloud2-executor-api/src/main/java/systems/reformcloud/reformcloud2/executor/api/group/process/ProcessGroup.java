/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.executor.api.group.process;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.builder.ProcessGroupBuilder;
import systems.reformcloud.reformcloud2.executor.api.configuration.data.JsonDataHolder;
import systems.reformcloud.reformcloud2.executor.api.group.process.player.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.process.startup.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.group.template.TemplateHolder;
import systems.reformcloud.reformcloud2.executor.api.network.data.SerializableObject;
import systems.reformcloud.reformcloud2.executor.api.process.builder.ProcessBuilder;
import systems.reformcloud.reformcloud2.executor.api.utility.name.Nameable;

import java.util.Optional;

public interface ProcessGroup extends Nameable, TemplateHolder, JsonDataHolder<ProcessGroup>, SerializableObject, Cloneable {

  @NotNull
  @Contract(value = "_ -> new", pure = true)
  static ProcessGroupBuilder builder(@NotNull String name) {
    return ExecutorAPI.getInstance().getProcessGroupProvider().createProcessGroup(name);
  }

  @NotNull
  static Optional<ProcessGroup> getByName(@NotNull String name) {
    return ExecutorAPI.getInstance().getProcessGroupProvider().getProcessGroup(name);
  }

  @NotNull
  StartupConfiguration getStartupConfiguration();

  void setStartupConfiguration(@NotNull StartupConfiguration startupConfiguration);

  @NotNull
  PlayerAccessConfiguration getPlayerAccessConfiguration();

  void setPlayerAccessConfiguration(@NotNull PlayerAccessConfiguration playerAccessConfiguration);

  boolean showIdInName();

  void setShowIdInName(boolean showIdInName);

  boolean createsStaticProcesses();

  void setCreatesStaticProcesses(boolean createsStaticProcesses);

  boolean isLobbyGroup();

  void setLobbyGroup(boolean lobbyGroup);

  @NotNull
  ProcessBuilder newProcess();

  void update();

  @NotNull
  ProcessGroup clone();
}
