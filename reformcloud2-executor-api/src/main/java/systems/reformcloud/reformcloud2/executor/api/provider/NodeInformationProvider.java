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

public interface NodeInformationProvider {

    @NotNull
    Optional<NodeProcessWrapper> getNodeInformation(@NotNull String name);

    @NotNull
    Optional<NodeProcessWrapper> getNodeInformation(@NotNull UUID nodeUniqueId);

    @NotNull
    @UnmodifiableView Collection<String> getNodeNames();

    @NotNull
    @UnmodifiableView Collection<UUID> getNodeUniqueIds();

    @NotNull
    @UnmodifiableView Collection<NodeInformation> getNodes();

    boolean isNodePresent(@NotNull String name);

    boolean isNodePresent(@NotNull UUID nodeUniqueId);

    @NotNull
    default Task<Optional<NodeProcessWrapper>> getNodeInformationAsync(@NotNull String name) {
        return Task.supply(() -> this.getNodeInformation(name));
    }

    @NotNull
    default Task<Optional<NodeProcessWrapper>> getNodeInformationAsync(@NotNull UUID nodeUniqueId) {
        return Task.supply(() -> this.getNodeInformation(nodeUniqueId));
    }

    @NotNull
    default Task<Collection<String>> getNodeNamesAsync() {
        return Task.supply(this::getNodeNames);
    }

    @NotNull
    default Task<Collection<UUID>> getNodeUniqueIdsAsync() {
        return Task.supply(this::getNodeUniqueIds);
    }

    @NotNull
    default Task<Collection<NodeInformation>> getNodesAsync() {
        return Task.supply(this::getNodes);
    }

    @NotNull
    default Task<Boolean> isNodePresentAsync(@NotNull String name) {
        return Task.supply(() -> this.isNodePresent(name));
    }

    @NotNull
    default Task<Boolean> isNodePresentAsync(@NotNull UUID nodeUniqueId) {
        return Task.supply(() -> this.isNodePresent(nodeUniqueId));
    }
}
