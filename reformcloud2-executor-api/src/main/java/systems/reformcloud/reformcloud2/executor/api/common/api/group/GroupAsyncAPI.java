package systems.reformcloud.reformcloud2.executor.api.common.api.group;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.List;

public interface GroupAsyncAPI extends GroupSyncAPI {

    /**
     * Creates a new main group
     *
     * @param name The name of the group
     * @return A task which will be completed with the created main group
     *
     * @see #createMainGroupAsync(String, List)
     */
    @Nonnull
    @CheckReturnValue
    Task<MainGroup> createMainGroupAsync(@Nonnull String name);

    /**
     * Creates a new main group
     *
     * @param name The name of the group
     * @param subgroups The subgroups of the new main group
     * @return A task which will be completed with the created main group
     */
    @Nonnull
    @CheckReturnValue
    Task<MainGroup> createMainGroupAsync(@Nonnull String name, @Nonnull List<String> subgroups);

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @return A task which will be completed with the created process group
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessGroup> createProcessGroupAsync(@Nonnull String name);

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @param templates The templates which should be used for the new group
     * @return A task which will be completed with the created process group
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessGroup> createProcessGroupAsync(@Nonnull String name, @Nonnull List<Template> templates);

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @param templates The templates which should be used for the new group
     * @param startupConfiguration The startup config of the new process group
     * @return A task which will be completed with the created process group
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessGroup> createProcessGroupAsync(
            @Nonnull String name,
            @Nonnull List<Template> templates,
            @Nonnull StartupConfiguration startupConfiguration
    );

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @param templates The templates which should be used for the new group
     * @param startupConfiguration The startup config of the new process group
     * @param playerAccessConfiguration The new player access configuration of the process group
     * @return A task which will be completed with the created process group
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessGroup> createProcessGroupAsync(
            @Nonnull String name,
            @Nonnull List<Template> templates,
            @Nonnull StartupConfiguration startupConfiguration,
            @Nonnull PlayerAccessConfiguration playerAccessConfiguration
    );

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @param templates The templates which should be used for the new group
     * @param startupConfiguration The startup config of the new process group
     * @param playerAccessConfiguration The new player access configuration of the process group
     * @param staticGroup {@code true} if the process group should be static
     * @return A task which will be completed with the created process group
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessGroup> createProcessGroupAsync(
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
     * @return A task which will be completed with the created process group
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessGroup> createProcessGroupAsync(@Nonnull ProcessGroup processGroup);

    /**
     * Updates a main group
     *
     * @param mainGroup The main group which should be updated
     * @return A task which will be completed with the new main group after the update
     */
    @Nonnull
    @CheckReturnValue
    Task<MainGroup> updateMainGroupAsync(@Nonnull MainGroup mainGroup);

    /**
     * Updates a process group
     *
     * @param processGroup The process group which should be updated
     * @return A task which will be completed with the process group after the update
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessGroup> updateProcessGroupAsync(@Nonnull ProcessGroup processGroup);

    /**
     * Get a main group
     *
     * @param name The name of the main group which should be found
     * @return A task which will be completed with the main group or {@code null} if the group does not exists
     */
    @Nonnull
    @CheckReturnValue
    Task<MainGroup> getMainGroupAsync(@Nonnull String name);

    /**
     * Get a process group
     *
     * @param name The name of the process group which should be found
     * @return A task which will be completed with the process group or {@code null} if the process group does not exists
     */
    @Nonnull
    @CheckReturnValue
    Task<ProcessGroup> getProcessGroupAsync(@Nonnull String name);

    /**
     * Deletes a main group
     *
     * @param name The name of the group which should be deleted
     * @return A task which will be completed after the successful delete of the group
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> deleteMainGroupAsync(@Nonnull String name);

    /**
     * Deletes a process group
     *
     * @param name The name of the group which should be deleted
     * @return A task which will be completed after the successful delete of the group
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> deleteProcessGroupAsync(@Nonnull String name);

    /**
     * Gets all main groups
     *
     * @return A task which will be completed with all main groups
     */
    @Nonnull
    @CheckReturnValue
    Task<List<MainGroup>> getMainGroupsAsync();

    /**
     * Gets all process groups
     *
     * @return A task which will be completed with all process groups
     */
    @Nonnull
    @CheckReturnValue
    Task<List<ProcessGroup>> getProcessGroupsAsync();
}
