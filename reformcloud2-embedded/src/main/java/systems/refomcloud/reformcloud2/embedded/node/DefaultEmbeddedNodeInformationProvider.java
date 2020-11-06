/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
import systems.reformcloud.reformcloud2.shared.node.DefaultNodeInformation;
import systems.reformcloud.reformcloud2.executor.api.provider.NodeInformationProvider;
import systems.reformcloud.reformcloud2.executor.api.wrappers.NodeProcessWrapper;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetNodeInformationByName;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetNodeInformationByUniqueId;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetNodeInformationResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetNodeNames;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetNodeNamesResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetNodeObjects;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetNodeObjectsResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetNodeUniqueIds;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetNodeUniqueIdsResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeIsNodePresentByName;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeIsNodePresentByUniqueId;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeIsNodePresentResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class DefaultEmbeddedNodeInformationProvider implements NodeInformationProvider {

    @NotNull
    @Override
    public Optional<NodeProcessWrapper> getNodeInformation(@NotNull String name) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetNodeInformationByName(name))
            .map(result -> {
                if (result instanceof ApiToNodeGetNodeInformationResult) {
                    DefaultNodeInformation information = ((ApiToNodeGetNodeInformationResult) result).getNodeInformation();
                    return information == null
                        ? Optional.<NodeProcessWrapper>empty()
                        : Optional.<NodeProcessWrapper>of(new DefaultEmbeddedNodeProcessWrapper(information));
                }

                return Optional.<NodeProcessWrapper>empty();
            }).orElseGet(Optional::empty);
    }

    @NotNull
    @Override
    public Optional<NodeProcessWrapper> getNodeInformation(@NotNull UUID nodeUniqueId) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetNodeInformationByUniqueId(nodeUniqueId))
            .map(result -> {
                if (result instanceof ApiToNodeGetNodeInformationResult) {
                    DefaultNodeInformation information = ((ApiToNodeGetNodeInformationResult) result).getNodeInformation();
                    return information == null
                        ? Optional.<NodeProcessWrapper>empty()
                        : Optional.<NodeProcessWrapper>of(new DefaultEmbeddedNodeProcessWrapper(information));
                }

                return Optional.<NodeProcessWrapper>empty();
            }).orElseGet(Optional::empty);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<String> getNodeNames() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetNodeNames())
            .map(result -> {
                if (result instanceof ApiToNodeGetNodeNamesResult) {
                    return ((ApiToNodeGetNodeNamesResult) result).getNames();
                }

                return new ArrayList<String>();
            }).orElseGet(Collections::emptyList);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<UUID> getNodeUniqueIds() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetNodeUniqueIds())
            .map(result -> {
                if (result instanceof ApiToNodeGetNodeUniqueIdsResult) {
                    return ((ApiToNodeGetNodeUniqueIdsResult) result).getUniqueIds();
                }

                return new ArrayList<UUID>();
            }).orElseGet(Collections::emptyList);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<DefaultNodeInformation> getNodes() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetNodeObjects())
            .map(result -> {
                if (result instanceof ApiToNodeGetNodeObjectsResult) {
                    return ((ApiToNodeGetNodeObjectsResult) result).getNodeInformation();
                }

                return new ArrayList<DefaultNodeInformation>();
            }).orElseGet(Collections::emptyList);
    }

    @Override
    public boolean isNodePresent(@NotNull String name) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeIsNodePresentByName(name))
            .map(result -> {
                if (result instanceof ApiToNodeIsNodePresentResult) {
                    return ((ApiToNodeIsNodePresentResult) result).isPresent();
                }

                return false;
            }).orElseGet(() -> false);
    }

    @Override
    public boolean isNodePresent(@NotNull UUID nodeUniqueId) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeIsNodePresentByUniqueId(nodeUniqueId))
            .map(result -> {
                if (result instanceof ApiToNodeIsNodePresentResult) {
                    return ((ApiToNodeIsNodePresentResult) result).isPresent();
                }

                return false;
            }).orElseGet(() -> false);
    }
}
