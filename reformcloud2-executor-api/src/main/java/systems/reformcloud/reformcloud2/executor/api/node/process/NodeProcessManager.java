package systems.reformcloud.reformcloud2.executor.api.node.process;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.utility.update.Updateable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public interface NodeProcessManager extends Updateable<ProcessInformation>, Iterable<ProcessInformation> {

    /**
     * Gets a locally running process
     *
     * @param name The name of the process which is requested
     * @return The locally running process or {@code null} if either the process is not running or not local
     */
    @Nullable
    ProcessInformation getLocalCloudProcess(@Nonnull String name);

    /**
     * Gets a locally running process
     *
     * @param uuid The unique id of the process which is requested
     * @return The locally running process or {@code null} if either the process is not running or not local
     */
    @Nullable
    ProcessInformation getLocalCloudProcess(@Nonnull UUID uuid);

    /**
     * Creates a new running process based in the provided group and template
     *
     * @param processGroup The group on which the process is based
     * @param template     The template which should get used as the main template
     * @param data         The extra data for the process api
     * @param uniqueID     The unique id of the process which should get used
     * @param start        If the process should get started after the prepare
     * @return A new created process information with the provided data
     */
    @Nonnull
    ProcessInformation prepareLocalProcess(@Nonnull ProcessGroup processGroup, @Nonnull Template template,
                                           @Nonnull JsonConfiguration data, @Nonnull UUID uniqueID, boolean start);

    /**
     * Starts a running processes based on the given process information
     *
     * @param processInformation The process information which is the base of the process
     * @param start              If the process should get started after the prepare
     * @return The process information which got modified
     */
    @Nonnull
    ProcessInformation prepareLocalProcess(@Nonnull ProcessInformation processInformation, boolean start);

    /**
     * Stops a local process
     *
     * @param name The name of the process which should get stopped
     * @return The last known process information or {@code null} if the process is not running or not local
     */
    @Nullable
    ProcessInformation stopLocalProcess(@Nonnull String name);

    /**
     * Stops a local process
     *
     * @param uuid The unique id of the process which should get stopped
     * @return The last known process information or {@code null} if the process is not running or not local
     */
    @Nullable
    ProcessInformation stopLocalProcess(@Nonnull UUID uuid);

    /**
     * Queues a specific process on another node
     *
     * @param processGroup The group on which the process is based
     * @param template     The template which should get used as the main template
     * @param data         The extra data for the process api
     * @param node         The node on which the process should get started
     * @param uniqueID     The unique id of the process which should get used
     * @param start        If the process should get started after the prepare
     * @return A new created process information with the provided data
     */
    @Nonnull
    ProcessInformation queueProcess(@Nonnull ProcessGroup processGroup, @Nonnull Template template,
                                    @Nonnull JsonConfiguration data, @Nonnull NodeInformation node,
                                    @Nonnull UUID uniqueID, boolean start);

    /**
     * Registers the running process as a local running process
     *
     * @param process The process which should get registered
     */
    void registerLocalProcess(@Nonnull RunningProcess process);

    /**
     * Unregisters the local process by the unique id of the process
     *
     * @param uniqueID The unique id of the process
     */
    void unregisterLocalProcess(@Nonnull UUID uniqueID);

    /**
     * Handles the start of a local process
     *
     * @param processInformation The process information of the processes which is just started
     */
    void handleLocalProcessStart(@Nonnull ProcessInformation processInformation);

    /**
     * Handles the stop of a local process
     *
     * @param processInformation The process information of the process which just stopped
     */
    void handleLocalProcessStop(@Nonnull ProcessInformation processInformation);

    /**
     * Handles the process start of a process from the cluster
     *
     * @param processInformation The process which is just started in the cluster
     */
    void handleProcessStart(@Nonnull ProcessInformation processInformation);

    /**
     * Handles the update of a process
     *
     * @param processInformation The information of the process which updated
     */
    void handleProcessUpdate(@Nonnull ProcessInformation processInformation);

    /**
     * Handles the connection of a process to the network
     *
     * @param processInformation The information of the process which connected in the network
     */
    void handleProcessConnection(@Nonnull ProcessInformation processInformation);

    /**
     * Handles the stop of a non-local process
     *
     * @param processInformation The information of the process which just stopped
     */
    void handleProcessStop(@Nonnull ProcessInformation processInformation);

    /**
     * Handles the unexpected disconnect of a process from the network
     *
     * @param name The name of the process which disconnected
     */
    void handleProcessDisconnect(@Nonnull String name);

    /**
     * Checks if a process is local
     *
     * @param name The name of the process
     * @return If the process by the name if locally running
     */
    boolean isLocal(@Nonnull String name);

    /**
     * Checks if a process is local
     *
     * @param uniqueID The unique id of the process
     * @return If the process by the name if locally running
     */
    boolean isLocal(@Nonnull UUID uniqueID);

    /**
     * @return All processes which are currently running in the cluster
     */
    @Nonnull
    Collection<ProcessInformation> getClusterProcesses();

    /**
     * Get all running processes of a specific group in the cluster
     *
     * @param group The name of the group which should get filtered
     * @return All processes of the specified group which are currently running in the cluster
     */
    @Nonnull
    Collection<ProcessInformation> getClusterProcesses(@Nonnull String group);

    /**
     * @return All processes which are locally running on the node
     */
    @Nonnull
    Collection<ProcessInformation> getLocalProcesses();

    /**
     * Get a process which is running in the cluster
     *
     * @param name The name of the process
     * @return The process which is running in the cluster or {@code null} if no process with the name is running
     */
    @Nullable
    ProcessInformation getClusterProcess(@Nonnull String name);

    /**
     * Get a process which is running in the cluster
     *
     * @param uniqueID The unique id of the process
     * @return The process which is running in the cluster or {@code null} if no process with the name is running
     */
    @Nullable
    ProcessInformation getClusterProcess(@Nonnull UUID uniqueID);
}
