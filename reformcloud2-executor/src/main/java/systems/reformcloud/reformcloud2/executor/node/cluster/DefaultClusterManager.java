package systems.reformcloud.reformcloud2.executor.node.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterFirewall;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

public class DefaultClusterManager implements ClusterManager {

    public DefaultClusterManager(ClusterFirewall clusterFirewall) {
        this.clusterFirewall = clusterFirewall;
    }

    private final ClusterFirewall clusterFirewall;

    private final Collection<NodeInformation> nodeInformation = new ArrayList<>();

    private NodeInformation head;

    @Override
    public void handleNodeDisconnect(InternalNetworkCluster cluster, String name) {
        Links.allOf(nodeInformation, e -> e.getName().equals(name)).forEach(nodeInformation::remove);
        recalculateHead();
    }

    @Override
    public void handleConnect(InternalNetworkCluster cluster, PacketSender node, NodeInformation nodeInformation, BiConsumer<Boolean, String> result) {
        if (this.nodeInformation.stream().anyMatch(e -> e.getName().equals(nodeInformation.getName()))) {
            result.accept(false, "A node with this name is already connected");
            return;
        }

        if (clusterFirewall.isIPAddressWhitelisted(node.getAddress())) {
            this.nodeInformation.add(nodeInformation);
            recalculateHead();
            result.accept(true, null);
            return;
        }

        result.accept(false, "IP address is not whitelisted");
    }

    @Override
    public NodeInformation getHeadNode() {
        if (head == null) {
            recalculateHead();
        }

        return head;
    }

    private void recalculateHead() {
        for (NodeInformation information : nodeInformation) {
            if (head == null) {
                head = information;
                continue;
            }

            if (information.getStartupTime() < head.getStartupTime()) {
                head = information;
            }
        }
    }
}
