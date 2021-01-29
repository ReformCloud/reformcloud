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
package systems.reformcloud.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.group.template.version.Version;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.process.builder.ProcessBuilder;
import systems.reformcloud.task.Task;
import systems.reformcloud.wrappers.ProcessWrapper;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides accessibility to utility methods for handling and managing {@link ProcessInformation}.
 */
public interface ProcessProvider {

  /**
   * Get a process by it's name
   *
   * @param name The name of the process to get
   * @return An optional process wrapper which is present if the process actually exists
   */
  @NotNull
  Optional<ProcessWrapper> getProcessByName(@NotNull String name);

  /**
   * Get a process by it's unique id
   *
   * @param uniqueId The unique id of the process to get
   * @return An optional process wrapper which is present if the process actually exists
   */
  @NotNull
  Optional<ProcessWrapper> getProcessByUniqueId(@NotNull UUID uniqueId);

  /**
   * Constructs a new builder for a process
   *
   * @return A new process builder
   */
  @NotNull
  ProcessBuilder createProcess();

  /**
   * @return An unmodifiable collection of all process objects
   */
  @NotNull
  @UnmodifiableView Collection<ProcessInformation> getProcesses();

  /**
   * Get all processes which are based on the given process group
   *
   * @param processGroup The name of the process group
   * @return An unmodifiable collection of all process objects
   */
  @NotNull
  @UnmodifiableView Collection<ProcessInformation> getProcessesByProcessGroup(@NotNull String processGroup);

  /**
   * Get all processes which are based on a process group which is a sub group of the provided main group
   *
   * @param mainGroup The name of the main group
   * @return An unmodifiable collection of all process objects
   */
  @NotNull
  @UnmodifiableView Collection<ProcessInformation> getProcessesByMainGroup(@NotNull String mainGroup);

  /**
   * Get all processes which are based on the given version
   *
   * @param version The version
   * @return An unmodifiable collection of all process objects
   */
  @NotNull
  @UnmodifiableView Collection<ProcessInformation> getProcessesByVersion(@NotNull Version version);

  /**
   * @return An unmodifiable collection of all process unique ids
   */
  @NotNull
  @UnmodifiableView Collection<UUID> getProcessUniqueIds();

  /**
   * @return The amount of registered processes
   */
  long getProcessCount();

  /**
   * Get the process count of all processes which are based on the given process group
   *
   * @param processGroup The name of the process group
   * @return The amount of processes which are based on the given process group
   */
  long getProcessCount(@NotNull String processGroup);

  /**
   * Updates the given process information globally. If the process is not registered anymore this
   * method has no effect
   *
   * @param processInformation The process information to update
   */
  void updateProcessInformation(@NotNull ProcessInformation processInformation);

  /**
   * This method does the same as {@link #getProcessByName(String)} but asynchronously.
   *
   * @param name The name of the process to get
   * @return An optional process wrapper which is present if the process actually exists
   */
  @NotNull
  default Task<Optional<ProcessWrapper>> getProcessByNameAsync(@NotNull String name) {
    return Task.supply(() -> this.getProcessByName(name));
  }

  /**
   * This method does the same as {@link #getProcessByUniqueId(UUID)} but asynchronously.
   *
   * @param processUniqueId The unique id of the process to get
   * @return An optional process wrapper which is present if the process actually exists
   */
  @NotNull
  default Task<Optional<ProcessWrapper>> getProcessByUniqueIdAsync(@NotNull UUID processUniqueId) {
    return Task.supply(() -> this.getProcessByUniqueId(processUniqueId));
  }

  /**
   * This method does the same as {@link #createProcess()} but asynchronously.
   *
   * @return A new process builder
   */
  @NotNull
  default Task<ProcessBuilder> createProcessAsync() {
    return Task.supply(this::createProcess);
  }

  /**
   * This method does the same as {@link #getProcesses()} but asynchronously.
   *
   * @return An unmodifiable collection of all process objects
   */
  @NotNull
  default Task<Collection<ProcessInformation>> getProcessesAsync() {
    return Task.supply(this::getProcesses);
  }

  /**
   * This method does the same as {@link #getProcessesByProcessGroup(String)} but asynchronously.
   *
   * @param processGroup The name of the process group
   * @return An unmodifiable collection of all process objects
   */
  @NotNull
  default Task<Collection<ProcessInformation>> getProcessesByProcessGroupAsync(@NotNull String processGroup) {
    return Task.supply(() -> this.getProcessesByProcessGroup(processGroup));
  }

  /**
   * This method does the same as {@link #getProcessesByMainGroup(String)} but asynchronously.
   *
   * @param mainGroup The name of the main group
   * @return An unmodifiable collection of all process objects
   */
  @NotNull
  default Task<Collection<ProcessInformation>> getProcessesByMainGroupAsync(@NotNull String mainGroup) {
    return Task.supply(() -> this.getProcessesByMainGroup(mainGroup));
  }

  /**
   * This method does the same as {@link #getProcessesByVersion(Version)} but asynchronously.
   *
   * @param version The version
   * @return An unmodifiable collection of all process objects
   */
  @NotNull
  default Task<Collection<ProcessInformation>> getProcessesByVersionAsync(@NotNull Version version) {
    return Task.supply(() -> this.getProcessesByVersion(version));
  }

  /**
   * This method does the same as {@link #getProcessUniqueIds()} but asynchronously.
   *
   * @return An unmodifiable collection of all process unique ids
   */
  @NotNull
  default Task<Collection<UUID>> getProcessUniqueIdsAsync() {
    return Task.supply(this::getProcessUniqueIds);
  }

  /**
   * This method does the same as {@link #getProcessCount()} but asynchronously.
   *
   * @return The amount of registered processes
   */
  @NotNull
  default Task<Long> getProcessCountAsync() {
    return Task.supply(this::getProcessCount);
  }

  /**
   * This method does the same as {@link #getProcessCount(String)} but asynchronously.
   *
   * @param processGroup The name of the process group
   * @return The amount of processes which are based on the given process group
   */
  @NotNull
  default Task<Long> getProcessCountAsync(@NotNull String processGroup) {
    return Task.supply(() -> this.getProcessCount(processGroup));
  }

  /**
   * This method does the same as {@link #updateProcessInformation(ProcessInformation)} but asynchronously.
   *
   * @param processInformation The process information to update
   * @return A task completed after updating the process or directly if there is no need for a blocking operation
   */
  @NotNull
  default Task<Void> updateProcessInformationAsync(@NotNull ProcessInformation processInformation) {
    return Task.supply(() -> {
      this.updateProcessInformation(processInformation);
      return null;
    });
  }
}
