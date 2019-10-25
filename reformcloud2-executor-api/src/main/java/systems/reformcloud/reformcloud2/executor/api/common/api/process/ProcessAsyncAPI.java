package systems.reformcloud.reformcloud2.executor.api.common.api.process;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProcessAsyncAPI extends ProcessSyncAPI {

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    Task<ProcessInformation> startProcessAsync(String groupName);

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @param template The template which should be used
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    Task<ProcessInformation> startProcessAsync(String groupName, String template);

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @param template The template which should be used
     * @param configurable The data for the process
     * @return A task which which will be completed with the created {@link ProcessInformation}
     */
    Task<ProcessInformation> startProcessAsync(String groupName, String template, JsonConfiguration configurable);

    /**
     * Stops a process
     *
     * @param name The name of the process
     * @return A task which will be completed with the last {@link ProcessInformation}
     */
    Task<ProcessInformation> stopProcessAsync(String name);

    /**
     * Stops a process
     *
     * @param uniqueID The uniqueID of the process
     * @return A task which will be completed with the last {@link ProcessInformation}
     */
    Task<ProcessInformation> stopProcessAsync(UUID uniqueID);

    /**
     * Gets a process
     *
     * @param name The name of the process
     * @return A task which will be completed with the {@link ProcessInformation} of the process or {@code null} if the process does not exists
     */
    Task<ProcessInformation> getProcessAsync(String name);

    /**
     * Gets a process
     *
     * @param uniqueID The uniqueID of the process
     * @return A task which will be completed with the {@link ProcessInformation} of the process or {@code null} if the process does not exists
     */
    Task<ProcessInformation> getProcessAsync(UUID uniqueID);

    /**
     * Get all processes
     *
     * @return A task with all started processes
     */
    Task<List<ProcessInformation>> getAllProcessesAsync();

    /**
     * Get all processes of a specific group
     *
     * @param group The group which should be searched for
     * @return A task with all started processes of the specified groups
     */
    Task<List<ProcessInformation>> getProcessesAsync(String group);

    /**
     * Executes a command on a process
     *
     * @param name The name of the process
     * @param commandLine The command line with should be executed
     * @return A task which will be completed after the packet sent
     */
    Task<Void> executeProcessCommandAsync(String name, String commandLine);

    /**
     * Gets the global online count
     *
     * @param ignoredProxies The ignored proxies
     * @return A task which will be completed with the global online count
     */
    Task<Integer> getGlobalOnlineCountAsync(Collection<String> ignoredProxies);

    /**
     * Get the current process information
     *
     * @return A task with will be completed with the current {@link ProcessInformation} or {@code null} if the runtime is not a process
     */
    Task<ProcessInformation> getThisProcessInformationAsync();
}
