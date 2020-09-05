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
package systems.reformcloud.reformcloud2.node.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.provider.NodeInformationProvider;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.wrappers.NodeProcessWrapper;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultNodeNodeInformationProvider implements NodeInformationProvider {

    private final Collection<DefaultNodeProcessWrapper> nodeInformation = new CopyOnWriteArrayList<>();

    public DefaultNodeNodeInformationProvider(@NotNull NodeInformation currentNode) {
        this.nodeInformation.add(new LocalNodeProcessWrapper(currentNode));
    }

    @NotNull
    @Override
    public Optional<NodeProcessWrapper> getNodeInformation(@NotNull String name) {
        for (NodeProcessWrapper information : this.nodeInformation) {
            if (information.getNodeInformation().getName().equals(name)) {
                return Optional.of(information);
            }
        }

        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<NodeProcessWrapper> getNodeInformation(@NotNull UUID nodeUniqueId) {
        for (NodeProcessWrapper information : this.nodeInformation) {
            if (information.getNodeInformation().getNodeUniqueID().equals(nodeUniqueId)) {
                return Optional.of(information);
            }
        }

        return Optional.empty();
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<String> getNodeNames() {
        return Streams.newCollection(this.nodeInformation, e -> e.getNodeInformation().getName());
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<UUID> getNodeUniqueIds() {
        return Streams.newCollection(this.nodeInformation, e -> e.getNodeInformation().getNodeUniqueID());
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<NodeInformation> getNodes() {
        return Streams.map(this.nodeInformation, DefaultNodeProcessWrapper::getNodeInformation);
    }

    @Override
    public boolean isNodePresent(@NotNull String name) {
        return this.getNodeInformation(name).isPresent();
    }

    @Override
    public boolean isNodePresent(@NotNull UUID nodeUniqueId) {
        return this.getNodeInformation(nodeUniqueId).isPresent();
    }

    public void updateNode(@NotNull NodeInformation nodeInformation) {
        for (DefaultNodeProcessWrapper defaultNodeProcessWrapper : this.nodeInformation) {
            if (defaultNodeProcessWrapper.nodeInformation.getNodeUniqueID().equals(nodeInformation.getNodeUniqueID())) {
                defaultNodeProcessWrapper.updateNodeInformation(nodeInformation);
                break;
            }
        }
    }

    public void removeNode(@NotNull String name) {
        this.nodeInformation.removeIf(wrapper -> wrapper.nodeInformation.getName().equals(name));
    }

    public void addNode(@NotNull NodeInformation nodeInformation) {
        this.nodeInformation.add(new DefaultNodeProcessWrapper(nodeInformation));
    }
}
