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
package systems.refomcloud.reformcloud2.embedded.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.wrappers.NodeProcessWrapper;
import systems.reformcloud.reformcloud2.protocol.node.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class DefaultEmbeddedNodeProcessWrapper implements NodeProcessWrapper {

    DefaultEmbeddedNodeProcessWrapper(NodeInformation nodeInformation) {
        this.nodeInformation = nodeInformation;
    }

    private NodeInformation nodeInformation;

    @NotNull
    @Override
    public NodeInformation getNodeInformation() {
        return this.nodeInformation;
    }

    @NotNull
    @Override
    public Optional<NodeInformation> requestNodeInformationUpdate() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeRequestNodeInformationUpdate(this.nodeInformation.getNodeUniqueID()))
                .map(result -> {
                    if (result instanceof ApiToNodeGetNodeInformationResult) {
                        ApiToNodeGetNodeInformationResult packet = (ApiToNodeGetNodeInformationResult) result;
                        return packet.getNodeInformation() == null
                                ? Optional.<NodeInformation>empty()
                                : Optional.of(this.nodeInformation = packet.getNodeInformation());
                    }

                    return Optional.<NodeInformation>empty();
                }).orElseGet(Optional::empty);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<String> sendCommandLine(@NotNull String commandLine) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeDispatchCommandLine(this.nodeInformation.getNodeUniqueID(), commandLine))
                .map(result -> {
                    if (result instanceof ApiToNodeGetStringCollectionResult) {
                        return ((ApiToNodeGetStringCollectionResult) result).getResult();
                    }

                    return new ArrayList<String>();
                }).orElseGet(Collections::emptyList);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<String> tabCompleteCommandLine(@NotNull String commandLine) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeCompleteCommandLine(this.nodeInformation.getNodeUniqueID(), commandLine))
                .map(result -> {
                    if (result instanceof ApiToNodeGetStringCollectionResult) {
                        return ((ApiToNodeGetStringCollectionResult) result).getResult();
                    }

                    return new ArrayList<String>();
                }).orElseGet(Collections::emptyList);
    }
}
