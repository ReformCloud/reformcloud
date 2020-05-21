/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.executor.api.common.api.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;

import java.util.List;
import java.util.function.Consumer;

public interface GroupSyncAPI {

    /**
     * Creates a new main group
     *
     * @param name The name of the group
     * @return The created main group
     */
    @NotNull
    MainGroup createMainGroup(@NotNull String name);

    /**
     * Creates a new main group
     *
     * @param name      The name of the group
     * @param subgroups The subgroups of the new main group
     * @return The created main group
     */
    @NotNull
    MainGroup createMainGroup(@NotNull String name, @NotNull List<String> subgroups);

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @return The created process group
     */
    @NotNull
    ProcessGroup createProcessGroup(@NotNull String name);

    /**
     * Creates a new process group
     *
     * @param name      The name of the new group
     * @param templates The templates which should be used for the new group
     * @return The created process group
     */
    @NotNull
    ProcessGroup createProcessGroup(@NotNull String name, @NotNull List<Template> templates);

    /**
     * Creates a new process group
     *
     * @param name                 The name of the new group
     * @param templates            The templates which should be used for the new group
     * @param startupConfiguration The startup config of the new process group
     * @return The created process group
     */
    @NotNull
    ProcessGroup createProcessGroup(
            @NotNull String name,
            @NotNull List<Template> templates,
            @NotNull StartupConfiguration startupConfiguration
    );

    /**
     * Creates a new process group
     *
     * @param name                      The name of the new group
     * @param templates                 The templates which should be used for the new group
     * @param startupConfiguration      The startup config of the new process group
     * @param playerAccessConfiguration The new player access configuration of the process group
     * @return The created process group
     */
    @NotNull
    ProcessGroup createProcessGroup(
            @NotNull String name,
            @NotNull List<Template> templates,
            @NotNull StartupConfiguration startupConfiguration,
            @NotNull PlayerAccessConfiguration playerAccessConfiguration
    );

    /**
     * Creates a new process group
     *
     * @param name                      The name of the new group
     * @param templates                 The templates which should be used for the new group
     * @param startupConfiguration      The startup config of the new process group
     * @param playerAccessConfiguration The new player access configuration of the process group
     * @param staticGroup               {@code true} if the process group should be static
     * @return The created process group
     */
    @NotNull
    ProcessGroup createProcessGroup(
            @NotNull String name,
            @NotNull List<Template> templates,
            @NotNull StartupConfiguration startupConfiguration,
            @NotNull PlayerAccessConfiguration playerAccessConfiguration,
            boolean staticGroup
    );

    /**
     * Creates a new process group
     *
     * @param processGroup The new process group
     * @return The created process group
     */
    @NotNull
    ProcessGroup createProcessGroup(@NotNull ProcessGroup processGroup);

    /**
     * Updates a main group
     *
     * @param mainGroup The main group which should be updated
     * @return The new main group after the update
     */
    @NotNull
    MainGroup updateMainGroup(@NotNull MainGroup mainGroup);

    /**
     * Updates a process group
     *
     * @param processGroup The process group which should be updated
     * @return The process group after the update
     */
    @NotNull
    ProcessGroup updateProcessGroup(@NotNull ProcessGroup processGroup);

    /**
     * Get a main group
     *
     * @param name The name of the main group which should be found
     * @return The main group or {@code null} if the group does not exists
     */
    @Nullable
    MainGroup getMainGroup(@NotNull String name);

    /**
     * Get a process group
     *
     * @param name The name of the process group which should be found
     * @return The process group or {@code null} if the process group does not exists
     */
    @Nullable
    ProcessGroup getProcessGroup(@NotNull String name);

    /**
     * Deletes a main group
     *
     * @param name The name of the group which should be deleted
     */
    void deleteMainGroup(@NotNull String name);

    /**
     * Deletes a process group
     *
     * @param name The name of the group which should be deleted
     */
    void deleteProcessGroup(@NotNull String name);

    /**
     * Gets all main groups
     *
     * @return All main groups
     */
    @NotNull
    List<MainGroup> getMainGroups();

    /**
     * Gets all process groups
     *
     * @return All process groups
     */
    @NotNull
    List<ProcessGroup> getProcessGroups();

    /**
     * Iterates through all process groups
     *
     * @param action The consumer which will handle all process groups
     */
    default void forEachProcessGroups(@NotNull Consumer<ProcessGroup> action) {
        this.getProcessGroups().forEach(action);
    }

    /**
     * Iterates through all main groups
     *
     * @param action The consumer which will handle all main groups
     */
    default void forEachMainGroups(@NotNull Consumer<MainGroup> action) {
        this.getMainGroups().forEach(action);
    }
}
