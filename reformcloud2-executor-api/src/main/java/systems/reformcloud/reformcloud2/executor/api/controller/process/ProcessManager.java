package systems.reformcloud.reformcloud2.executor.api.controller.process;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.update.Updateable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface ProcessManager extends Iterable<ProcessInformation>, Updateable<ProcessInformation> {

    /**
     * Gets all currently running processes
     * <p>
     *
     * @return All running processes
     */
    List<ProcessInformation> getAllProcesses();

    /**
     * Gets all running processes of a specific group
     * <p>
     *
     * @param group The name of the group
     * @return A list which contains all process information about the running process
     * @see ProcessInformation
     */
    List<ProcessInformation> getProcesses(String group);

    /**
     * Gets the current online and waiting process count of a specific group
     * <p>
     *
     * @param group The name of the group which should be filtered (required to be non-null)
     * @return The online and waiting process count of an specific group
     */
    Long getOnlineAndWaitingProcessCount(String group);

    /**
     * Gets the current waiting process count of a specific group
     * <p>
     *
     * @param group The name of the group which should be filtered (required to be non-null)
     * @return The online and waiting process count of an specific group
     */
    Integer getWaitingProcesses(String group);

    /**
     * Gets a specific process by the name
     * <p>
     *
     * @param name The name of the process
     * @return The filtered process information or {@code null} if the process does not exists
     * @see ProcessInformation
     * @see #getProcess(UUID)
     */
    @Nullable
    ProcessInformation getProcess(String name);

    /**
     * Gets a specific process by the process uniqueID
     * <p>
     *
     * @param uniqueID The unique id of the process
     * @return The current process information or {@code null} if the process does not exists
     * @see ProcessInformation
     * @see #getProcess(String)
     */
    @Nullable
    ProcessInformation getProcess(UUID uniqueID);

    /**
     * Starts a process of the specified group and filters automatically the best template
     * <p>
     *
     * @param groupName The name of the group which is required to be non-null
     * @return The process information about the created process
     * @see ProcessInformation
     * @see #startProcess(String, String)
     */
    @Nullable
    ProcessInformation startProcess(String groupName);

    /**
     * Starts a process with the given template name
     * <p>
     *
     * @param groupName The name if tge group which is required to be non-null
     * @param template  The name of the template which must not exists using default template instead
     * @return The process information of the created process
     * @see ProcessInformation
     * @see #startProcess(String, String, JsonConfiguration)
     */
    @Nullable
    ProcessInformation startProcess(String groupName, String template);

    /**
     * Starts a process with the given template name and the json configuration as extra data for the process object
     * <p>
     *
     * @param groupName    The name if tge group which is required to be non-null
     * @param template     The name of the template which must not exists using default template instead
     * @param configurable The extra data which you can read on the process (can be {@code null})
     * @return The process information of the created process
     * @see ProcessInformation
     */
    @Nullable
    ProcessInformation startProcess(String groupName, String template, JsonConfiguration configurable);

    /**
     * Starts a prepared process
     *
     * @param processInformation The information of the prepared process
     * @return The process information after the start call
     */
    @Nonnull
    ProcessInformation startProcess(@Nonnull ProcessInformation processInformation);

    /**
     * Prepares a process with the given template name and the json configuration as extra data for the process object
     * <p>
     * @param groupName    The name if tge group which is required to be non-null
     * @param template     The name of the template which must not exists using default template instead
     * @param configurable The extra data which you can read on the process (can be {@code null})
     * @return The process information of the created process
     * @see ProcessInformation
     */
    @Nullable
    ProcessInformation prepareProcess(String groupName, String template, JsonConfiguration configurable);

    /**
     * Stops a specific process and returns the last known process information
     * <p>
     *
     * @param name The name of the running process which is required to be non-null
     * @return The last known process information or {@code null} if the process is unknown
     * @see ProcessInformation
     * @see #stopProcess(UUID)
     */
    @Nullable
    ProcessInformation stopProcess(String name);

    /**
     * Stops a specific process and returns the last known process information
     * <p>
     *
     * @param uniqueID The unique process id which is required to be non-null
     * @return The last known process information or {@code null} if the process is unknown
     * @see #stopProcess(String)
     */
    @Nullable
    ProcessInformation stopProcess(UUID uniqueID);

    /**
     * This method will be called when a client disconnects, to remove all running processes and
     * let the cloud move them to the next client
     * <p>
     *
     * @param clientName The name of the client which is disconnected
     * @see #onChannelClose(String)
     */
    void onClientDisconnect(String clientName);

    /**
     * This method gets called when a channel closes
     * <p>
     *
     * @param name The name of the channel sender
     * @see #onClientDisconnect(String)
     */
    void onChannelClose(String name);

    /**
     * Unregisters a specific by the given uuid
     * <p>
     *
     * @param uniqueID The uniqueID of the process information which will be used to identify the process
     * @see ProcessInformation#getProcessUniqueID()
     */
    void unregisterProcess(UUID uniqueID);
}
