package systems.reformcloud.reformcloud2.executor.node.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class DefaultNodeInternalCluster implements InternalNetworkCluster {

    private final Collection<NodeInformation> connectedNodes = new CopyOnWriteArrayList<>();

    public DefaultNodeInternalCluster(ClusterManager clusterManager, NodeInformation self, PacketHandler packetHandler) {
        this.clusterManager = clusterManager;
        this.packetHandler = packetHandler;
        this.self = self;
    }

    private final ClusterManager clusterManager;

    private final PacketHandler packetHandler;

    private NodeInformation self;

    @Override
    public ClusterManager getClusterManager() {
        return clusterManager;
    }

    @Override
    public NodeInformation getHeadNode() {
        return clusterManager.getHeadNode();
    }

    @Override
    public NodeInformation getSelfNode() {
        return self;
    }

    @Override
    public void updateSelf(NodeInformation self) {
        Conditions.isTrue(self != null);
        this.self = self;
    }

    @Override
    public NodeInformation getNode(String name) {
        return Streams.filterToReference(connectedNodes, e -> e.getName().equals(name)).orNothing();
    }

    @Override
    public Collection<NodeInformation> getConnectedNodes() {
        return connectedNodes;
    }

    @Override
    public void handleNodeUpdate(NodeInformation nodeInformation) {
        Streams.filterToReference(connectedNodes, e -> e.getNodeUniqueID().equals(nodeInformation.getNodeUniqueID())).ifPresent(e -> {
            this.connectedNodes.add(nodeInformation);
            this.connectedNodes.remove(e);
        });
    }

    @Override
    public void publishToHeadNode(Packet packet) {
        if (getHeadNode() == null) {
            return;
        }

        DefaultChannelManager.INSTANCE.get(getHeadNode().getName()).ifPresent(sender -> sender.sendPacket(packet));
    }

    @Override
    public void broadCastToCluster(Packet packet) {
        getConnectedNodes()
                .stream()
                .map(e -> DefaultChannelManager.INSTANCE.get(e.getName()).orNothing())
                .filter(Objects::nonNull)
                .forEach(e -> e.sendPacket(packet));
    }

    @Override
    public <T> T sendQueryToNode(String node, Packet query, Function<Packet, T> responseHandler) {
        PacketSender sender = DefaultChannelManager.INSTANCE.get(node).orNothing();
        if (sender == null) {
            return null;
        }

        Packet result = packetHandler.getQueryHandler().sendQueryAsync(sender, query).getTask().getUninterruptedly();
        return result != null ? responseHandler.apply(result) : null;
    }

    @Override
    public NodeInformation findBestNodeForStartup(ProcessGroup group, Template template) {
        AtomicReference<NodeInformation> result = new AtomicReference<>();
        if (self.getUsedMemory() + template.getRuntimeConfiguration().getMaxMemory() < self.getMaxMemory()) {
            result.set(self);
        }

        getConnectedNodes().forEach(e -> {
            if (!group.getStartupConfiguration().isSearchBestClientAlone()
                    && !group.getStartupConfiguration().getUseOnlyTheseClients().contains(e.getName())) {
                return;
            }

            if (result.get() == null) {
                if (e.getUsedMemory() + template.getRuntimeConfiguration().getMaxMemory() < e.getMaxMemory()) {
                    result.set(e);
                }

                return;
            }

            NodeInformation current = result.get();
            long memoryOnCurrentNode = calcMem(current.getUsedMemory(), template);
            long memoryOnNewNode = calcMem(e.getUsedMemory(), template);

            if (memoryOnNewNode <= e.getMaxMemory() && memoryOnCurrentNode > memoryOnNewNode
                    && memoryOnCurrentNode <= current.getMaxMemory()) {
                result.set(e);
            }
        });

        return result.get();
    }

    private long calcMem(long used, Template template) {
        return used + template.getRuntimeConfiguration().getMaxMemory();
    }
}
