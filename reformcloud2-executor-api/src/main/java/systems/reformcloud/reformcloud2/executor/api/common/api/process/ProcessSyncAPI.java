package systems.reformcloud.reformcloud2.executor.api.common.api.process;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface ProcessSyncAPI {

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @return The created {@link ProcessInformation}
     */
    ProcessInformation startProcess(String groupName);

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @param template The template which should be used
     * @return The created {@link ProcessInformation}
     */
    ProcessInformation startProcess(String groupName, String template);

    /**
     * Starts a process
     *
     * @param groupName The name of the group which should be started from
     * @param template The template which should be used
     * @param configurable The data for the process
     * @return The created {@link ProcessInformation}
     */
    ProcessInformation startProcess(String groupName, String template, JsonConfiguration configurable);

    /**
     * Stops a process
     *
     * @param name The name of the process
     * @return The last known {@link ProcessInformation}
     */
    ProcessInformation stopProcess(String name);

    /**
     * Stops a process
     *
     * @param uniqueID The uniqueID of the process
     * @return The last {@link ProcessInformation}
     */
    ProcessInformation stopProcess(UUID uniqueID);

    /**
     * Gets a process
     *
     * @param name The name of the process
     * @return The {@link ProcessInformation} of the process or {@code null} if the process does not exists
     */
    ProcessInformation getProcess(String name);

    /**
     * Gets a process
     *
     * @param uniqueID The uniqueID of the process
     * @return The {@link ProcessInformation} of the process or {@code null} if the process does not exists
     */
    ProcessInformation getProcess(UUID uniqueID);

    /**
     * Get all processes
     *
     * @return All started processes
     */
    List<ProcessInformation> getAllProcesses();


    /**
     * Get all processes of a specific group
     *
     * @param group The group which should be searched for
     * @return All started processes of the specified groups
     */
    List<ProcessInformation> getProcesses(String group);

    /**
     * Executes a command on a process
     *
     * @param name The name of the process
     * @param commandLine The command line with should be executed
     */
    void executeProcessCommand(String name, String commandLine);

    /**
     * Gets the global online count
     *
     * @param ignoredProxies The ignored proxies
     * @return The global online count
     */
    int getGlobalOnlineCount(Collection<String> ignoredProxies);

    /**
     * Get the current process information
     *
     * @return The current {@link ProcessInformation} or {@code null} if the runtime is not a process
     */
    ProcessInformation getThisProcessInformation();

    /**
     * Iterates through all {@link ProcessInformation}
     *
     * @param action The consumer which will accept by each {@link ProcessInformation}
     */
    default void forEach(Consumer<ProcessInformation> action) {
        getAllProcesses().forEach(action);
    }

    /**
     * Updates a specific {@link ProcessInformation}
     *
     * @param processInformation The process information which should be updated
     */
    void update(ProcessInformation processInformation);

    /**
     * Updates a specific {@link ProcessInformation}
     *
     * @param processInformation The process information which should be updated
     * @return A task which will be completed after the update of the {@link ProcessInformation}
     */
    default Task<Void> updateAsync(ProcessInformation processInformation) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            update(processInformation);
            task.complete(null);
        });
        return task;
    }
}
