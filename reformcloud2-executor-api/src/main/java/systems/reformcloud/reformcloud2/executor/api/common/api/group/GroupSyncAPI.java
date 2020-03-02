package systems.reformcloud.reformcloud2.executor.api.common.api.group;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public interface GroupSyncAPI {

    /**
     * Creates a new main group
     *
     * @param name The name of the group
     * @return The created main group
     */
    @Nonnull
    MainGroup createMainGroup(@Nonnull String name);

    /**
     * Creates a new main group
     *
     * @param name      The name of the group
     * @param subgroups The subgroups of the new main group
     * @return The created main group
     */
    @Nonnull
    MainGroup createMainGroup(@Nonnull String name, @Nonnull List<String> subgroups);

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @return The created process group
     */
    @Nonnull
    ProcessGroup createProcessGroup(@Nonnull String name);

    /**
     * Creates a new process group
     *
     * @param name      The name of the new group
     * @param templates The templates which should be used for the new group
     * @return The created process group
     */
    @Nonnull
    ProcessGroup createProcessGroup(@Nonnull String name, @Nonnull List<Template> templates);

    /**
     * Creates a new process group
     *
     * @param name                 The name of the new group
     * @param templates            The templates which should be used for the new group
     * @param startupConfiguration The startup config of the new process group
     * @return The created process group
     */
    @Nonnull
    ProcessGroup createProcessGroup(
            @Nonnull String name,
            @Nonnull List<Template> templates,
            @Nonnull StartupConfiguration startupConfiguration
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
    @Nonnull
    ProcessGroup createProcessGroup(
            @Nonnull String name,
            @Nonnull List<Template> templates,
            @Nonnull StartupConfiguration startupConfiguration,
            @Nonnull PlayerAccessConfiguration playerAccessConfiguration
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
    @Nonnull
    ProcessGroup createProcessGroup(
            @Nonnull String name,
            @Nonnull List<Template> templates,
            @Nonnull StartupConfiguration startupConfiguration,
            @Nonnull PlayerAccessConfiguration playerAccessConfiguration,
            boolean staticGroup
    );

    /**
     * Creates a new process group
     *
     * @param processGroup The new process group
     * @return The created process group
     */
    @Nonnull
    ProcessGroup createProcessGroup(@Nonnull ProcessGroup processGroup);

    /**
     * Updates a main group
     *
     * @param mainGroup The main group which should be updated
     * @return The new main group after the update
     */
    @Nonnull
    MainGroup updateMainGroup(@Nonnull MainGroup mainGroup);

    /**
     * Updates a process group
     *
     * @param processGroup The process group which should be updated
     * @return The process group after the update
     */
    @Nonnull
    ProcessGroup updateProcessGroup(@Nonnull ProcessGroup processGroup);

    /**
     * Get a main group
     *
     * @param name The name of the main group which should be found
     * @return The main group or {@code null} if the group does not exists
     */
    @Nullable
    MainGroup getMainGroup(@Nonnull String name);

    /**
     * Get a process group
     *
     * @param name The name of the process group which should be found
     * @return The process group or {@code null} if the process group does not exists
     */
    @Nullable
    ProcessGroup getProcessGroup(@Nonnull String name);

    /**
     * Deletes a main group
     *
     * @param name The name of the group which should be deleted
     */
    void deleteMainGroup(@Nonnull String name);

    /**
     * Deletes a process group
     *
     * @param name The name of the group which should be deleted
     */
    void deleteProcessGroup(@Nonnull String name);

    /**
     * Gets all main groups
     *
     * @return All main groups
     */
    @Nonnull
    List<MainGroup> getMainGroups();

    /**
     * Gets all process groups
     *
     * @return All process groups
     */
    @Nonnull
    List<ProcessGroup> getProcessGroups();

    /**
     * Iterates through all process groups
     *
     * @param action The consumer which will handle all process groups
     */
    default void forEachProcessGroups(@Nonnull Consumer<ProcessGroup> action) {
        getProcessGroups().forEach(action);
    }

    /**
     * Iterates through all main groups
     *
     * @param action The consumer which will handle all main groups
     */
    default void forEachMainGroups(@Nonnull Consumer<MainGroup> action) {
        getMainGroups().forEach(action);
    }
}
