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
package systems.reformcloud.reformcloud2.executor.node.cluster;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DefaultNodeInternalCluster implements InternalNetworkCluster {

    private final Collection<NodeInformation> connectedNodes = new CopyOnWriteArrayList<>();
    private final ClusterManager clusterManager;
    private final PacketHandler packetHandler;
    private NodeInformation self;

    public DefaultNodeInternalCluster(ClusterManager clusterManager, NodeInformation self, PacketHandler packetHandler) {
        this.clusterManager = clusterManager;
        this.packetHandler = packetHandler;
        this.self = self;
    }

    @NotNull
    @Override
    public ClusterManager getClusterManager() {
        return this.clusterManager;
    }

    @Override
    public NodeInformation getHeadNode() {
        return this.clusterManager.getHeadNode();
    }

    @NotNull
    @Override
    public NodeInformation getSelfNode() {
        return this.self;
    }

    @Override
    public void updateSelf(@NotNull NodeInformation self) {
        this.self = self;
    }

    @Override
    public NodeInformation getNode(@NotNull String name) {
        return Streams.filter(this.connectedNodes, e -> e.getName().equals(name));
    }

    @Nullable
    @Override
    public NodeInformation getNode(@NotNull UUID nodeUniqueID) {
        return Streams.filter(this.connectedNodes, e -> e.getNodeUniqueID().equals(nodeUniqueID));
    }

    @NotNull
    @Override
    public Collection<NodeInformation> getConnectedNodes() {
        return this.connectedNodes;
    }

    @Override
    public void handleNodeUpdate(@NotNull NodeInformation nodeInformation) {
        Streams
                .filterToReference(connectedNodes, e -> e.getNodeUniqueID().equals(nodeInformation.getNodeUniqueID()))
                .ifPresent(e -> {
                    this.connectedNodes.remove(e);
                    this.connectedNodes.add(nodeInformation);
                });

        if (this.getHeadNode() != null && this.getHeadNode().canEqual(nodeInformation)) {
            this.clusterManager.updateHeadNode(nodeInformation);
        }
    }

    @Override
    public void publishToHeadNode(@NotNull Packet packet) {
        if (this.getHeadNode() == null) {
            return;
        }

        DefaultChannelManager.INSTANCE.get(this.getHeadNode().getName()).ifPresent(sender -> sender.sendPacket(packet));
    }

    @Override
    public <T> T sendQueryToNode(@NotNull String node, @NotNull Packet query, @NotNull Function<Packet, T> responseHandler) {
        PacketSender sender = DefaultChannelManager.INSTANCE.get(node).orNothing();
        if (sender == null) {
            return null;
        }

        Packet result = this.packetHandler.getQueryHandler().sendQueryAsync(sender, query).getUninterruptedly();
        return result != null ? responseHandler.apply(result) : null;
    }

    @Override
    public NodeInformation findBestNodeForStartup(@NotNull ProcessGroup group, int maxMemory) {
        NodeInformation result = null;
        for (NodeInformation validNode : this.findValidNodes(group)) {
            long memoryAfterStart = validNode.getUsedMemory() + maxMemory;
            if (memoryAfterStart > validNode.getMaxMemory()) {
                continue;
            }

            if (result == null) {
                result = validNode;
                continue;
            }

            if (validNode.getProcessRuntimeInformation().getCpuUsageSystem() == -1
                    || result.getProcessRuntimeInformation().getCpuUsageSystem() == -1) {
                // do not check system cpu if it's not given
                if (result.getUsedMemory() > validNode.getUsedMemory()) {
                    result = validNode;
                }

                continue;
            }

            if (result.getProcessRuntimeInformation().getCpuUsageSystem() > validNode.getProcessRuntimeInformation().getCpuUsageSystem()
                    || result.getUsedMemory() > validNode.getUsedMemory()) {
                result = validNode;
            }
        }

        return result;
    }

    @NotNull
    private Collection<NodeInformation> findValidNodes(@NotNull ProcessGroup group) {
        Collection<NodeInformation> validClusterNodes = this.connectedNodes
                .stream()
                .filter(e -> DefaultChannelManager.INSTANCE.get(e.getName()).isPresent())
                .filter(e -> group.getStartupConfiguration().isSearchBestClientAlone()
                        || group.getStartupConfiguration().getUseOnlyTheseClients().contains(e.getName()))
                .collect(Collectors.toList());
        if (group.getStartupConfiguration().isSearchBestClientAlone()
                || group.getStartupConfiguration().getUseOnlyTheseClients().contains(this.getSelfNode().getName())) {
            validClusterNodes.add(this.getSelfNode());
        }

        return validClusterNodes;
    }
}
