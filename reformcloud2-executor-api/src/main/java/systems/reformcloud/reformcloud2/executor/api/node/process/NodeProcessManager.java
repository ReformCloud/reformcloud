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
package systems.reformcloud.reformcloud2.executor.api.node.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.utility.update.Updateable;

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
    ProcessInformation getLocalCloudProcess(@NotNull String name);

    /**
     * Gets a locally running process
     *
     * @param uniqueID The unique id of the process which is requested
     * @return The locally running process or {@code null} if either the process is not running or not local
     */
    @Nullable
    ProcessInformation getLocalCloudProcess(@NotNull UUID uniqueID);

    /**
     * Creates a new running process based in the provided group and template
     *
     * @param configuration The configuration of the new process
     * @param template      The template which should be used for the process
     * @param start         If the process should get started after the prepare
     * @return A new created process information with the provided data
     */
    @NotNull
    ProcessInformation prepareLocalProcess(@NotNull ProcessConfiguration configuration, @NotNull Template template, boolean start);

    /**
     * Starts a running processes based on the given process information
     *
     * @param processInformation The process information which is the base of the process
     * @param start              If the process should get started after the prepare
     * @return The process information which got modified
     */
    @NotNull
    ProcessInformation prepareLocalProcess(@NotNull ProcessInformation processInformation, boolean start);

    /**
     * Stops a local process
     *
     * @param name The name of the process which should get stopped
     * @return The last known process information or {@code null} if the process is not running or not local
     */
    @Nullable
    ProcessInformation stopLocalProcess(@NotNull String name);

    /**
     * Stops a local process
     *
     * @param uuid The unique id of the process which should get stopped
     * @return The last known process information or {@code null} if the process is not running or not local
     */
    @Nullable
    ProcessInformation stopLocalProcess(@NotNull UUID uuid);

    /**
     * Queues a specific process on another node
     *
     * @param configuration The configuration of the process
     * @param node          The node on which the process should get started
     * @param start         If the process should get started after the prepare
     * @return A new created process information with the provided data
     */
    @NotNull
    ProcessInformation queueProcess(@NotNull ProcessConfiguration configuration, @NotNull Template template, @NotNull NodeInformation node, boolean start);

    /**
     * Registers the running process as a local running process
     *
     * @param process The process which should get registered
     */
    void registerLocalProcess(@NotNull RunningProcess process);

    /**
     * Unregisters the local process by the unique id of the process
     *
     * @param uniqueID The unique id of the process
     */
    void unregisterLocalProcess(@NotNull UUID uniqueID);

    /**
     * Handles the start of a local process
     *
     * @param processInformation The process information of the processes which is just started
     */
    void handleLocalProcessStart(@NotNull ProcessInformation processInformation);

    /**
     * Handles the stop of a local process
     *
     * @param processInformation The process information of the process which just stopped
     */
    void handleLocalProcessStop(@NotNull ProcessInformation processInformation);

    /**
     * Handles the process start of a process from the cluster
     *
     * @param processInformation The process which is just started in the cluster
     */
    void handleProcessStart(@NotNull ProcessInformation processInformation);

    /**
     * Handles the update of a process
     *
     * @param processInformation The information of the process which updated
     */
    void handleProcessUpdate(@NotNull ProcessInformation processInformation);

    /**
     * Handles the connection of a process to the network
     *
     * @param processInformation The information of the process which connected in the network
     */
    void handleProcessConnection(@NotNull ProcessInformation processInformation);

    /**
     * Handles the stop of a non-local process
     *
     * @param processInformation The information of the process which just stopped
     */
    void handleProcessStop(@NotNull ProcessInformation processInformation);

    /**
     * Handles the unexpected disconnect of a process from the network
     *
     * @param name The name of the process which disconnected
     */
    void handleProcessDisconnect(@NotNull String name);

    /**
     * Checks if a process is local
     *
     * @param name The name of the process
     * @return If the process by the name if locally running
     */
    boolean isLocal(@NotNull String name);

    /**
     * Checks if a process is local
     *
     * @param uniqueID The unique id of the process
     * @return If the process by the name if locally running
     */
    boolean isLocal(@NotNull UUID uniqueID);

    /**
     * @return All processes which are currently running in the cluster
     */
    @NotNull
    Collection<ProcessInformation> getClusterProcesses();

    /**
     * Get all running processes of a specific group in the cluster
     *
     * @param group The name of the group which should get filtered
     * @return All processes of the specified group which are currently running in the cluster
     */
    @NotNull
    Collection<ProcessInformation> getClusterProcesses(@NotNull String group);

    /**
     * @return All processes which are locally running on the node
     */
    @NotNull
    Collection<ProcessInformation> getLocalProcesses();

    /**
     * Get a process which is running in the cluster
     *
     * @param name The name of the process
     * @return The process which is running in the cluster or {@code null} if no process with the name is running
     */
    @Nullable
    ProcessInformation getClusterProcess(@NotNull String name);

    /**
     * Get a process which is running in the cluster
     *
     * @param uniqueID The unique id of the process
     * @return The process which is running in the cluster or {@code null} if no process with the name is running
     */
    @Nullable
    ProcessInformation getClusterProcess(@NotNull UUID uniqueID);
}
