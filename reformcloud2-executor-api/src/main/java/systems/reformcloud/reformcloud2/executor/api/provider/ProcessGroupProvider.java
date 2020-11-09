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
package systems.reformcloud.reformcloud2.executor.api.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.builder.ProcessGroupBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.process.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.Collection;
import java.util.Optional;

/**
 * Provides accessibility to utility methods for handling and managing {@link ProcessGroup}.
 */
public interface ProcessGroupProvider {

    /**
     * Get a process group by it's name. The result is only present if the process group actually exists.
     *
     * @param name The name of the process group
     * @return An optional process group which has the same name as given
     */
    @NotNull
    Optional<ProcessGroup> getProcessGroup(@NotNull String name);

    /**
     * Deletes the process group if it exists.
     *
     * @param name The name if the process group to delete
     */
    void deleteProcessGroup(@NotNull String name);

    /**
     * Updates the specified process group object
     *
     * @param processGroup The process group which should get updated
     */
    void updateProcessGroup(@NotNull ProcessGroup processGroup);

    /**
     * @return An unmodifiable view of the registered process groups
     */
    @NotNull
    @UnmodifiableView Collection<ProcessGroup> getProcessGroups();

    /**
     * @return The amount of process groups which are registered
     */
    long getProcessGroupCount();

    /**
     * @return An unmodifiable view of all process group names
     */
    @NotNull
    @UnmodifiableView Collection<String> getProcessGroupNames();

    /**
     * Constructs a new builder for a process group by the given name
     *
     * @param name The name of the process group to create
     * @return A new process group builder
     */
    @NotNull
    ProcessGroupBuilder createProcessGroup(@NotNull String name);

    /**
     * This method does the same as {@link #getProcessGroup(String)} but asynchronously.
     *
     * @param name The name of the process group
     * @return An optional process group which has the same name as given
     */
    @NotNull
    default Task<Optional<ProcessGroup>> getProcessGroupAsync(@NotNull String name) {
        return Task.supply(() -> this.getProcessGroup(name));
    }

    /**
     * This method does the same as {@link #deleteProcessGroup(String)} but asynchronously.
     *
     * @param name The name if the process group to delete
     * @return A task completed after deleting the process group or directly if there is no need for a blocking operation
     */
    @NotNull
    default Task<Void> deleteProcessGroupAsync(@NotNull String name) {
        return Task.supply(() -> {
            this.deleteProcessGroup(name);
            return null;
        });
    }

    /**
     * This method does the same as {@link #updateProcessGroup(ProcessGroup)} but asynchronously.
     *
     * @param processGroup The process group which should get updated
     * @return A task completed after updating the process group or directly if there is no need for a blocking operation
     */
    @NotNull
    default Task<Void> updateProcessGroupAsync(@NotNull ProcessGroup processGroup) {
        return Task.supply(() -> {
            this.updateProcessGroup(processGroup);
            return null;
        });
    }

    /**
     * This method does the same as {@link #getProcessGroups()} but asynchronously.
     *
     * @return An unmodifiable view of the registered process groups
     */
    @NotNull
    default Task<Collection<ProcessGroup>> getProcessGroupsAsync() {
        return Task.supply(this::getProcessGroups);
    }

    /**
     * This method does the same as {@link #getProcessGroupCount()} but asynchronously.
     *
     * @return The amount of process groups which are registered
     */
    @NotNull
    default Task<Long> getProcessGroupCountAsync() {
        return Task.supply(this::getProcessGroupCount);
    }

    /**
     * This method does the same as {@link #getProcessGroupNames()} but asynchronously.
     *
     * @return An unmodifiable view of all process group names
     */
    @NotNull
    default Task<Collection<String>> getProcessGroupNamesAsync() {
        return Task.supply(this::getProcessGroupNames);
    }
}
