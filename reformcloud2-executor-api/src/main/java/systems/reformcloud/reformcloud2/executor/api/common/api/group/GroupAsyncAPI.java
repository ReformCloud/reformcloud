package systems.reformcloud.reformcloud2.executor.api.common.api.group;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.List;

public interface GroupAsyncAPI {

    /**
     * Creates a new main group
     *
     * @param name The name of the group
     * @return A task which will be completed with the created main group
     * @see #createMainGroupAsync(String, List)
     */
    @NotNull
    Task<MainGroup> createMainGroupAsync(@NotNull String name);

    /**
     * Creates a new main group
     *
     * @param name      The name of the group
     * @param subgroups The subgroups of the new main group
     * @return A task which will be completed with the created main group
     */
    @NotNull
    Task<MainGroup> createMainGroupAsync(@NotNull String name, @NotNull List<String> subgroups);

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @return A task which will be completed with the created process group
     */
    @NotNull
    Task<ProcessGroup> createProcessGroupAsync(@NotNull String name);

    /**
     * Creates a new process group
     *
     * @param name      The name of the new group
     * @param templates The templates which should be used for the new group
     * @return A task which will be completed with the created process group
     */
    @NotNull
    Task<ProcessGroup> createProcessGroupAsync(@NotNull String name, @NotNull List<Template> templates);

    /**
     * Creates a new process group
     *
     * @param name                 The name of the new group
     * @param templates            The templates which should be used for the new group
     * @param startupConfiguration The startup config of the new process group
     * @return A task which will be completed with the created process group
     */
    @NotNull
    Task<ProcessGroup> createProcessGroupAsync(
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
     * @return A task which will be completed with the created process group
     */
    @NotNull
    Task<ProcessGroup> createProcessGroupAsync(
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
     * @return A task which will be completed with the created process group
     */
    @NotNull
    Task<ProcessGroup> createProcessGroupAsync(
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
     * @return A task which will be completed with the created process group
     */
    @NotNull
    Task<ProcessGroup> createProcessGroupAsync(@NotNull ProcessGroup processGroup);

    /**
     * Updates a main group
     *
     * @param mainGroup The main group which should be updated
     * @return A task which will be completed with the new main group after the update
     */
    @NotNull
    Task<MainGroup> updateMainGroupAsync(@NotNull MainGroup mainGroup);

    /**
     * Updates a process group
     *
     * @param processGroup The process group which should be updated
     * @return A task which will be completed with the process group after the update
     */
    @NotNull
    Task<ProcessGroup> updateProcessGroupAsync(@NotNull ProcessGroup processGroup);

    /**
     * Get a main group
     *
     * @param name The name of the main group which should be found
     * @return A task which will be completed with the main group or {@code null} if the group does not exists
     */
    @NotNull
    Task<MainGroup> getMainGroupAsync(@NotNull String name);

    /**
     * Get a process group
     *
     * @param name The name of the process group which should be found
     * @return A task which will be completed with the process group or {@code null} if the process group does not exists
     */
    @NotNull
    Task<ProcessGroup> getProcessGroupAsync(@NotNull String name);

    /**
     * Deletes a main group
     *
     * @param name The name of the group which should be deleted
     * @return A task which will be completed after the successful delete of the group
     */
    @NotNull
    Task<Void> deleteMainGroupAsync(@NotNull String name);

    /**
     * Deletes a process group
     *
     * @param name The name of the group which should be deleted
     * @return A task which will be completed after the successful delete of the group
     */
    @NotNull
    Task<Void> deleteProcessGroupAsync(@NotNull String name);

    /**
     * Gets all main groups
     *
     * @return A task which will be completed with all main groups
     */
    @NotNull
    Task<List<MainGroup>> getMainGroupsAsync();

    /**
     * Gets all process groups
     *
     * @return A task which will be completed with all process groups
     */
    @NotNull
    Task<List<ProcessGroup>> getProcessGroupsAsync();
}
