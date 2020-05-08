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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

/**
 * Represents the manager of a cluster which handles the internal cluster
 */
public interface ClusterManager {

    /**
     * Initializes the current cluster manager
     */
    void init();

    /**
     * Handles the connection of a node to the cluster
     *
     * @param cluster         The cluster to which the node has connected
     * @param nodeInformation The information of the node which is connected
     */
    void handleConnect(@NotNull InternalNetworkCluster cluster, @NotNull NodeInformation nodeInformation);

    /**
     * Handles the disconnect of a node in the cluster
     *
     * @param cluster  The cluster from which the node has disconnected
     * @param nodeName The name of the node which disconnected
     */
    void handleNodeDisconnect(@NotNull InternalNetworkCluster cluster, @NotNull String nodeName);

    /**
     * Get all online and currently waiting processes of the specified group
     *
     * @param groupName The group of which the processes should be
     * @return The amount of online and waiting processes in the cluster
     */
    int getOnlineAndWaiting(@NotNull String groupName);

    /**
     * @return The information of the head node of the current cluster manager
     */
    @Nullable
    NodeInformation getHeadNode();

    /**
     * Updates the head node information
     *
     * @param newHeadNodeInformation The new node information of the head node
     */
    void updateHeadNode(@NotNull NodeInformation newHeadNodeInformation);
}
