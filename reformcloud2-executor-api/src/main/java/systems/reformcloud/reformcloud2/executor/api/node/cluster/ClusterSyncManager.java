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
package systems.reformcloud.reformcloud2.executor.api.node.cluster;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

import java.util.Collection;

/**
 * Handles all synchronisation actions in a cluster and makes the synchronisation actions
 */
public interface ClusterSyncManager {

    /**
     * Updates and syncs the current information about the self node into the cluster
     */
    void syncSelfInformation();

    /**
     * Syncs the start of a process into the cluster
     *
     * @param processInformation The information of the process which is started
     */
    void syncProcessStartup(@NotNull ProcessInformation processInformation);

    /**
     * Syncs the update of a process into the cluster
     *
     * @param processInformation The information of the process which has updated it's information
     */
    void syncProcessUpdate(@NotNull ProcessInformation processInformation);

    /**
     * Syncs the stop of a process into the cluster
     *
     * @param processInformation The information of the process which is stopped
     */
    void syncProcessStop(@NotNull ProcessInformation processInformation);

    /**
     * Syncs all process groups of the current node into the cluster
     *
     * @param processGroups The groups which should get synced
     * @param action        The action which should get executed with the groups on the other nodes
     *                      in the cluster
     */
    void syncProcessGroups(@NotNull Collection<ProcessGroup> processGroups, @NotNull SyncAction action);

    /**
     * Syncs all main groups from the current node into the cluster
     *
     * @param mainGroups The main group which should get synced
     * @param action     The action which should get executed with the groups on the other nodes
     *                   in the cluster
     */
    void syncMainGroups(Collection<MainGroup> mainGroups, SyncAction action);

    /**
     * Syncs all process information from the current node into the cluster
     *
     * @param information All process which should get synced to the other nodes in the cluster
     */
    void syncProcessInformation(@NotNull Collection<ProcessInformation> information);

    /**
     * @return All process groups synced from the other nodes
     */
    @NotNull
    Collection<ProcessGroup> getProcessGroups();

    /**
     * @return All main groups synced from the other nodes
     */
    @NotNull
    Collection<MainGroup> getMainGroups();

    /**
     * Checks if a process groups already exists in the cluster
     *
     * @param name The name of the process group
     * @return If the process group already exists
     */
    boolean existsProcessGroup(@NotNull String name);

    /**
     * Checks if a main group already exists in the cluster
     *
     * @param name The name of the main group
     * @return If the main group already exists
     */
    boolean existsMainGroup(@NotNull String name);

    /**
     * Syncs the creation of a new process group into the cluster
     *
     * @param group The group which got created
     */
    void syncProcessGroupCreate(@NotNull ProcessGroup group);

    /**
     * Syncs the creation of a new main group into the cluster
     *
     * @param group The group which got created
     */
    void syncMainGroupCreate(@NotNull MainGroup group);

    /**
     * Syncs the update of a process group into the cluster
     *
     * @param processGroup The group which got updated
     */
    void syncProcessGroupUpdate(@NotNull ProcessGroup processGroup);

    /**
     * Syncs the update of a main group into the cluster
     *
     * @param mainGroup The main group which got updated
     */
    void syncMainGroupUpdate(@NotNull MainGroup mainGroup);

    /**
     * Syncs the deletion of a process group into the cluster
     *
     * @param name The name of the group which got deleted
     */
    void syncProcessGroupDelete(@NotNull String name);

    /**
     * Syncs the deletion of a main group into the cluster
     *
     * @param name The name of the main group which got deleted
     */
    void syncMainGroupDelete(@NotNull String name);

    /**
     * Handles the sync of the process groups from another node and creates or updates the group from
     * the sync
     *
     * @param groups All groups which got synced from the other node
     * @param action The action which should get executed with the groups
     */
    @ApiStatus.Internal
    void handleProcessGroupSync(@NotNull Collection<ProcessGroup> groups, @NotNull SyncAction action);

    /**
     * Handles the sync of the main groups from another node and creates or updates the groups from the
     * sync
     *
     * @param groups All groups which got synced from the other node
     * @param action The action which should get executed with the groups
     */
    @ApiStatus.Internal
    void handleMainGroupSync(@NotNull Collection<MainGroup> groups, @NotNull SyncAction action);

    /**
     * Handles the sync of all processes from another node
     *
     * @param information All processes which got synced
     */
    @ApiStatus.Internal
    void handleProcessInformationSync(@NotNull Collection<ProcessInformation> information);

    /**
     * Handles the reload request from another node in the cluster and reloads the current node
     */
    @ApiStatus.Internal
    void handleClusterReload();

    /**
     * Handles the update of another node in the cluster
     *
     * @param nodeInformation The node information which got synced
     */
    @ApiStatus.Internal
    void handleNodeInformationUpdate(@NotNull NodeInformation nodeInformation);

    /**
     * Reloads the complete cluster (and all nodes in it)
     */
    @ApiStatus.Internal
    void doClusterReload();

    /**
     * Disconnects the current node from the cluster
     */
    @ApiStatus.Internal
    void disconnectFromCluster();

    /**
     * @return If the node is completely connected to all other nodes in the cluster
     */
    boolean isConnectedAndSyncWithCluster();

    /**
     * @return All connections (ip) for which the node is waiting
     */
    @NotNull
    Collection<String> getWaitingConnections();
}
