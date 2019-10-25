package systems.reformcloud.reformcloud2.executor.api.common.api.group;

import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.StartupConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

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
    Task<MainGroup> createMainGroupAsync(String name);

    /**
     * Creates a new main group
     *
     * @param name The name of the group
     * @param subgroups The subgroups of the new main group
     * @return A task which will be completed with the created main group
     */
    Task<MainGroup> createMainGroupAsync(String name, List<String> subgroups);

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @return A task which will be completed with the created process group
     */
    Task<ProcessGroup> createProcessGroupAsync(String name);

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @param parent The parent-{@link MainGroup} group of the new group
     * @return A task which will be completed with the created process group
     */
    Task<ProcessGroup> createProcessGroupAsync(String name, String parent);

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @param parent The parent-{@link MainGroup} group of the new group
     * @param templates The templates which should be used for the new group
     * @return A task which will be completed with the created process group
     */
    Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates);

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @param parent The parent-{@link MainGroup} group of the new group
     * @param templates The templates which should be used for the new group
     * @param startupConfiguration The startup config of the new process group
     * @return A task which will be completed with the created process group
     */
    Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates,
                                    StartupConfiguration startupConfiguration);

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @param parent The parent-{@link MainGroup} group of the new group
     * @param templates The templates which should be used for the new group
     * @param startupConfiguration The startup config of the new process group
     * @param playerAccessConfiguration The new player access configuration of the process group
     * @return A task which will be completed with the created process group
     */
    Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates,
                                    StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration);

    /**
     * Creates a new process group
     *
     * @param name The name of the new group
     * @param parent The parent-{@link MainGroup} group of the new group
     * @param templates The templates which should be used for the new group
     * @param startupConfiguration The startup config of the new process group
     * @param playerAccessConfiguration The new player access configuration of the process group
     * @param staticGroup {@code true} if the process group should be static
     * @return A task which will be completed with the created process group
     */
    Task<ProcessGroup> createProcessGroupAsync(String name, String parent, List<Template> templates,
                                    StartupConfiguration startupConfiguration, PlayerAccessConfiguration playerAccessConfiguration,
                                    boolean staticGroup);

    /**
     * Creates a new process group
     *
     * @param processGroup The new process group
     * @return A task which will be completed with the created process group
     */
    Task<ProcessGroup> createProcessGroupAsync(ProcessGroup processGroup);

    /**
     * Updates a main group
     *
     * @param mainGroup The main group which should be updated
     * @return A task which will be completed with the new main group after the update
     */
    Task<MainGroup> updateMainGroupAsync(MainGroup mainGroup);

    /**
     * Updates a process group
     *
     * @param processGroup The process group which should be updated
     * @return A task which will be completed with the process group after the update
     */
    Task<ProcessGroup> updateProcessGroupAsync(ProcessGroup processGroup);

    /**
     * Get a main group
     *
     * @param name The name of the main group which should be found
     * @return A task which will be completed with the main group or {@code null} if the group does not exists
     */
    Task<MainGroup> getMainGroupAsync(String name);

    /**
     * Get a process group
     *
     * @param name The name of the process group which should be found
     * @return A task which will be completed with the process group or {@code null} if the process group does not exists
     */
    Task<ProcessGroup> getProcessGroupAsync(String name);

    /**
     * Deletes a main group
     *
     * @param name The name of the group which should be deleted
     * @return A task which will be completed after the successful delete of the group
     */
    Task<Void> deleteMainGroupAsync(String name);

    /**
     * Deletes a process group
     *
     * @param name The name of the group which should be deleted
     * @return A task which will be completed after the successful delete of the group
     */
    Task<Void> deleteProcessGroupAsync(String name);

    /**
     * Gets all main groups
     *
     * @return A task which will be completed with all main groups
     */
    Task<List<MainGroup>> getMainGroupsAsync();

    /**
     * Gets all process groups
     *
     * @return A task which will be completed with all process groups
     */
    Task<List<ProcessGroup>> getProcessGroupsAsync();
}
