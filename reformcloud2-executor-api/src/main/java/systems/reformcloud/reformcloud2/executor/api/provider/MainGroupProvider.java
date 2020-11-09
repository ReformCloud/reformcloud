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
import systems.reformcloud.reformcloud2.executor.api.builder.MainGroupBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.main.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.task.Task;

import java.util.Collection;
import java.util.Optional;

/**
 * Provides accessibility to utility methods for handling and managing {@link MainGroup}.
 */
public interface MainGroupProvider {

    /**
     * Get a main group by it's name. The result is only present if the main group actually exists.
     *
     * @param name The name of the main group
     * @return An optional main group which has the same name as given
     */
    @NotNull
    Optional<MainGroup> getMainGroup(@NotNull String name);

    /**
     * Deletes a main group if it exists.
     *
     * @param name The name of the main group to delete
     */
    void deleteMainGroup(@NotNull String name);

    /**
     * Deletes the specified main group object
     *
     * @param mainGroup The main group which should get updated
     */
    void updateMainGroup(@NotNull MainGroup mainGroup);

    /**
     * @return An unmodifiable view of the registered main groups
     */
    @NotNull
    @UnmodifiableView Collection<MainGroup> getMainGroups();

    /**
     * @return The amount of main groups which are registered
     */
    long getMainGroupCount();

    /**
     * @return An unmodifiable view of all main group names
     */
    @NotNull
    @UnmodifiableView Collection<String> getMainGroupNames();

    /**
     * Constructs a new builder for a main group by the given name
     *
     * @param name The name of the main group to create
     * @return A new main group builder
     */
    @NotNull
    MainGroupBuilder createMainGroup(@NotNull String name);

    /**
     * This method does the same as {@link #getMainGroup(String)} but asynchronously.
     *
     * @param name The name of the main group
     * @return An optional main group which has the same name as given
     */
    @NotNull
    default Task<Optional<MainGroup>> getMainGroupAsync(@NotNull String name) {
        return Task.supply(() -> this.getMainGroup(name));
    }

    /**
     * This method does the same as {@link #deleteMainGroup(String)} but asynchronously.
     *
     * @param name The name of the main group to delete
     * @return A task completed after deleting the main group or directly if there is no need for a blocking operation
     */
    @NotNull
    default Task<Void> deleteMainGroupAsync(@NotNull String name) {
        return Task.supply(() -> {
            this.deleteMainGroup(name);
            return null;
        });
    }

    /**
     * This method does the same as {@link #updateMainGroup(MainGroup)} but asynchronously.
     *
     * @param mainGroup The main group which should get deleted
     * @return A task completed after deleting the main group or directly if there is no need for a blocking operation
     */
    @NotNull
    default Task<Void> updateMainGroupAsync(@NotNull MainGroup mainGroup) {
        return Task.supply(() -> {
            this.updateMainGroup(mainGroup);
            return null;
        });
    }

    /**
     * This method does the same as {@link #getMainGroups()} but asynchronously.
     *
     * @return An unmodifiable view of the registered main groups
     */
    @NotNull
    default Task<Collection<MainGroup>> getMainGroupsAsync() {
        return Task.supply(this::getMainGroups);
    }

    /**
     * This method does the same as {@link #getMainGroupCount()} but asynchronously.
     *
     * @return The amount of main groups which are registered
     */
    @NotNull
    default Task<Long> getMainGroupCountAsync() {
        return Task.supply(this::getMainGroupCount);
    }

    /**
     * This method does the same as {@link #getMainGroupNames()} but asynchronously.
     *
     * @return An unmodifiable view of all main group names
     */
    @NotNull
    default Task<Collection<String>> getMainGroupNamesAsync() {
        return Task.supply(this::getMainGroupNames);
    }
}
