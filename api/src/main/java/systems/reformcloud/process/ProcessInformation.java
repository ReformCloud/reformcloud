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
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.configuration.data.JsonDataHolder;
import systems.reformcloud.functional.Sorted;
import systems.reformcloud.group.process.ProcessGroup;
import systems.reformcloud.group.template.Template;
import systems.reformcloud.network.address.NetworkAddress;
import systems.reformcloud.network.data.SerializableObject;
import systems.reformcloud.process.builder.ProcessBuilder;
import systems.reformcloud.utility.name.Nameable;
import systems.reformcloud.wrappers.ProcessWrapper;

import java.util.Optional;
import java.util.UUID;

/**
 * An information about a process.
 */
public interface ProcessInformation extends JsonDataHolder<ProcessInformation>, ProcessStateHolder, ProcessInclusionHolder, PlayerHolder, Nameable, Sorted<ProcessInformation>, SerializableObject, Cloneable {

  /**
   * Get a process by it's name.
   *
   * @param name The name of the process to get.
   * @return The process.
   */
  @NotNull
  static Optional<ProcessWrapper> getByName(@NotNull String name) {
    return ExecutorAPI.getInstance().getProcessProvider().getProcessByName(name);
  }

  /**
   * Get a process by it's unique id.
   *
   * @param uniqueId The unique id of the process to get.
   * @return The process.
   */
  @NotNull
  static Optional<ProcessWrapper> getByUniqueId(@NotNull UUID uniqueId) {
    return ExecutorAPI.getInstance().getProcessProvider().getProcessByUniqueId(uniqueId);
  }

  /**
   * Creates a process builder for the provided group name.
   *
   * @param group The group name to create a process for.
   * @return The created process builder.
   */
  @NotNull
  static ProcessBuilder builder(@NotNull String group) {
    return ExecutorAPI.getInstance().getProcessProvider().createProcess().group(group);
  }

  /**
   * Creates a process builder for the provided group.
   *
   * @param group The group to create a process for.
   * @return The created process builder.
   */
  @NotNull
  static ProcessBuilder builder(@NotNull ProcessGroup group) {
    return ExecutorAPI.getInstance().getProcessProvider().createProcess().group(group);
  }

  /**
   * Gets the identity of this process.
   *
   * @return The identity of this process.
   */
  @NotNull
  Identity getId();

  /**
   * Gets the host this process is bound to.
   *
   * @return The host this process is bound to.
   */
  @NotNull
  NetworkAddress getHost();

  /**
   * Gets the primary template of this process.
   *
   * @return The primary template of this process.
   */
  @NotNull
  Template getPrimaryTemplate();

  /**
   * Gets the process group this process is based on.
   *
   * @return The process group this process is based on.
   */
  @NotNull
  ProcessGroup getProcessGroup();

  /**
   * Sets the process group this process is based on.
   *
   * @param processGroup The process group this process is based on.
   */
  void setProcessGroup(@NotNull ProcessGroup processGroup);

  /**
   * Gets the runtime information of this process.
   *
   * @return The runtime information of this process.
   */
  @NotNull
  ProcessRuntimeInformation getRuntimeInformation();

  /**
   * Sets the runtime information of this process.
   *
   * @param information The runtime information of this process.
   */
  void setRuntimeInformation(@NotNull ProcessRuntimeInformation information);

  /**
   * Updates this process information.
   */
  void update();

  /**
   * Creates a clone of this process.
   *
   * @return A clone of this process.
   */
  @NotNull
  ProcessInformation clone();
}
