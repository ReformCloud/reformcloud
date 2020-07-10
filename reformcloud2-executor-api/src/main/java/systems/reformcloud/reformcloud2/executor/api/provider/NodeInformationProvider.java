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
package systems.reformcloud.reformcloud2.executor.api.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.wrappers.NodeProcessWrapper;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides accessibility to utility methods for handling and managing {@link NodeInformation}.
 */
public interface NodeInformationProvider {

    /**
     * Get a node by it's name. This is only present if the node exists and is currently connected to
     * the current node.
     *
     * @param name The name of the node to get
     * @return An optional node process wrapper which has the same name as given
     */
    @NotNull
    Optional<NodeProcessWrapper> getNodeInformation(@NotNull String name);

    /**
     * Get a node by it's name. This is only present if the node exists and is currently connected to
     * the current node.
     *
     * @param nodeUniqueId The unique id of the node to get
     * @return An optional node process wrapper which has the same unique id as given
     */
    @NotNull
    Optional<NodeProcessWrapper> getNodeInformation(@NotNull UUID nodeUniqueId);

    /**
     * @return All names of all nodes which are currently connected to the current node
     */
    @NotNull
    @UnmodifiableView Collection<String> getNodeNames();

    /**
     * @return All unique ids of all nodes which are currently connected to the current node
     */
    @NotNull
    @UnmodifiableView Collection<UUID> getNodeUniqueIds();

    /**
     * @return All node information objects of the nodes which are currently connected to the current node
     */
    @NotNull
    @UnmodifiableView Collection<NodeInformation> getNodes();

    /**
     * Checks if the node by the name if currently connected to the network
     *
     * @param name The name of the node to check
     * @return {@code true} if the node is currently connected, {@code false} otherwise
     */
    boolean isNodePresent(@NotNull String name);

    /**
     * Checks if the node by the unique id if currently connected to the network
     *
     * @param nodeUniqueId The unique id of the node to check
     * @return {@code true} if the node is currently connected, {@code false} otherwise
     */
    boolean isNodePresent(@NotNull UUID nodeUniqueId);

    /**
     * This method does the same as {@link #getNodeInformation(String)} but asynchronously.
     *
     * @param name The name of the node to get
     * @return An optional node process wrapper which has the same name as given
     */
    @NotNull
    default Task<Optional<NodeProcessWrapper>> getNodeInformationAsync(@NotNull String name) {
        return Task.supply(() -> this.getNodeInformation(name));
    }

    /**
     * This method does the same as {@link #getNodeInformation(UUID)} but asynchronously.
     *
     * @param nodeUniqueId The unique id of the node to get
     * @return An optional node process wrapper which has the same unique id as given
     */
    @NotNull
    default Task<Optional<NodeProcessWrapper>> getNodeInformationAsync(@NotNull UUID nodeUniqueId) {
        return Task.supply(() -> this.getNodeInformation(nodeUniqueId));
    }

    /**
     * This method does the same as {@link #getNodeNames()} but asynchronously.
     *
     * @return All names of all nodes which are currently connected to the current node
     */
    @NotNull
    default Task<Collection<String>> getNodeNamesAsync() {
        return Task.supply(this::getNodeNames);
    }

    /**
     * This method does the same as {@link #getNodeUniqueIds()} but asynchronously.
     *
     * @return All unique ids of all nodes which are currently connected to the current node
     */
    @NotNull
    default Task<Collection<UUID>> getNodeUniqueIdsAsync() {
        return Task.supply(this::getNodeUniqueIds);
    }

    /**
     * This method does the same as {@link #getNodes()} but asynchronously.
     *
     * @return All node information objects of the nodes which are currently connected to the current node
     */
    @NotNull
    default Task<Collection<NodeInformation>> getNodesAsync() {
        return Task.supply(this::getNodes);
    }

    /**
     * This method does the same as {@link #isNodePresent(String)} but asynchronously.
     *
     * @param name The name of the node to check
     * @return {@code true} if the node is currently connected, {@code false} otherwise
     */
    @NotNull
    default Task<Boolean> isNodePresentAsync(@NotNull String name) {
        return Task.supply(() -> this.isNodePresent(name));
    }

    /**
     * This method does the same as {@link #isNodePresent(UUID)} but asynchronously.
     *
     * @param nodeUniqueId The unique id of the node to check
     * @return {@code true} if the node is currently connected, {@code false} otherwise
     */
    @NotNull
    default Task<Boolean> isNodePresentAsync(@NotNull UUID nodeUniqueId) {
        return Task.supply(() -> this.isNodePresent(nodeUniqueId));
    }
}
