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
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * Represents a cluster of nodes holding all available information about the cluster and the nodes in it
 */
public interface InternalNetworkCluster {

    /**
     * @return The cluster manager associated with the current cluster
     */
    @NotNull
    ClusterManager getClusterManager();

    /**
     * @return The cluster's head node or {@code null} if the node is not running in a cluster
     */
    @Nullable
    NodeInformation getHeadNode();

    /**
     * @return The information about the self node
     */
    @NotNull
    NodeInformation getSelfNode();

    /**
     * Updates the self information locally
     *
     * @param self The new node information
     */
    void updateSelf(@NotNull NodeInformation self);

    /**
     * @return If the current node is the head node in the cluster
     */
    default boolean isSelfNodeHead() {
        return this.getHeadNode() != null && this.getHeadNode().canEqual(this.getSelfNode());
    }

    /**
     * Get a node by it's name
     *
     * @param name The name of the node
     * @return The node information about the node with the same name or {@code null} if the node is unknown
     */
    @Nullable
    NodeInformation getNode(@NotNull String name);

    /**
     * Get a node by it's unique id
     *
     * @param nodeUniqueID The unique id of the node
     * @return The node information about the node with the same unique id or {@code null} if the node is unknown
     */
    @Nullable
    NodeInformation getNode(@NotNull UUID nodeUniqueID);

    /**
     * @return All nodes which are connected to the current node
     */
    @NotNull
    Collection<NodeInformation> getConnectedNodes();

    /**
     * Handles the cluster update of a node information
     *
     * @param nodeInformation The information about the node which updated it's information
     */
    void handleNodeUpdate(@NotNull NodeInformation nodeInformation);

    /**
     * @return If there are no other connected nodes in the cluster
     */
    default boolean noOtherNodes() {
        return this.getConnectedNodes().isEmpty();
    }

    /**
     * Publishes a packet to the current head node
     *
     * @param packet The packet to sent to the head node
     */
    void publishToHeadNode(@NotNull Packet packet);

    /**
     * Sends a query to the head node
     *
     * @param query           The query packet which should get sent
     * @param responseHandler The handler of the packet response
     * @param <T>             The type of information which should get read from the packet
     * @return The information sent back by the head node and applied to the response handler
     */
    @Nullable
    default <T> T sendQueryToHead(@NotNull Packet query, @NotNull Function<Packet, T> responseHandler) {
        if (this.getHeadNode() == null || this.getHeadNode().canEqual(this.getSelfNode())) {
            return null;
        }

        return this.sendQueryToNode(this.getHeadNode().getName(), query, responseHandler);
    }

    /**
     * Sends the given packet to all nodes which are connected in the cluster
     *
     * @param packet The packet which should get sent to all nodes
     */
    default void broadCastToCluster(@NotNull Packet packet) {
        this.getConnectedNodes()
                .stream()
                .map(e -> DefaultChannelManager.INSTANCE.get(e.getName()).orNothing())
                .filter(Objects::nonNull)
                .forEach(e -> e.sendPacket(packet));
    }

    /**
     * Sends a query to the specified node
     *
     * @param nodeName        The name of the node to which the packet should get sent
     * @param query           The query packet which should get sent
     * @param responseHandler The handler of the packet response
     * @param <T>             The type of information which should get read from the packet
     * @return The information sent back by the given node and applied to the response handler
     */
    @Nullable
    <T> T sendQueryToNode(@NotNull String nodeName, @NotNull Packet query, @NotNull Function<Packet, T> responseHandler);

    /**
     * Searches the best node for the startup of a process
     *
     * @param group     The group for which a new process is should get started
     * @param maxMemory The maximum amount of memory which is assigned to the process
     * @return The node information which can start the process or {@code null} if no node is available to start the process
     */
    @Nullable
    NodeInformation findBestNodeForStartup(@NotNull ProcessGroup group, int maxMemory);
}
