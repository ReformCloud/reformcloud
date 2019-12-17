package systems.reformcloud.reformcloud2.executor.node.cluster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.handler.PacketHandler;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.InternalNetworkCluster;

public class DefaultNodeInternalCluster implements InternalNetworkCluster {

  private final Collection<NodeInformation> connectedNodes = new ArrayList<>();

  public DefaultNodeInternalCluster(ClusterManager clusterManager,
                                    NodeInformation self,
                                    PacketHandler packetHandler) {
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
    return Links
        .filterToReference(connectedNodes, e -> e.getName().equals(name))
        .orNothing();
  }

  @Override
  public Collection<NodeInformation> getConnectedNodes() {
    return connectedNodes;
  }

  @Override
  public void handleNodeUpdate(NodeInformation nodeInformation) {
    Links
        .filterToReference(
            connectedNodes,
            e -> e.getNodeUniqueID().equals(nodeInformation.getNodeUniqueID()))
        .ifPresent(e -> {
          this.connectedNodes.add(nodeInformation);
          this.connectedNodes.remove(e);
        });
  }

  @Override
  public void publishToHeadNode(Packet packet) {
    if (getHeadNode() == null) {
      return;
    }

    DefaultChannelManager.INSTANCE.get(getHeadNode().getName())
        .ifPresent(sender -> sender.sendPacket(packet));
  }

  @Override
  public void broadCastToCluster(Packet packet) {
    getConnectedNodes()
        .stream()
        .map(e -> DefaultChannelManager.INSTANCE.get(e.getName()).orNothing())
        .forEach(e -> {
          if (e != null) {
            e.sendPacket(packet);
          }
        });
  }

  @Override
  public <T> T sendQueryToNode(String node, Packet query,
                               Function<Packet, T> responseHandler) {
    PacketSender sender = DefaultChannelManager.INSTANCE.get(node).orNothing();
    if (sender == null) {
      return null;
    }

    Packet result = packetHandler.getQueryHandler()
                        .sendQueryAsync(sender, query)
                        .getTask()
                        .getUninterruptedly();
    return result != null ? responseHandler.apply(result) : null;
  }

  @Override
  public NodeInformation findBestNodeForStartup(Template template) {
    AtomicReference<NodeInformation> result = new AtomicReference<>();
    getConnectedNodes().forEach(e -> {
      if (result.get() == null) {
        if (e.getUsedMemory() +
                template.getRuntimeConfiguration().getMaxMemory() <
            e.getMaxMemory()) {
          result.set(e);
        }

        return;
      }

      NodeInformation current = result.get();
      if (e.getUsedMemory() +
                  template.getRuntimeConfiguration().getMaxMemory() <
              e.getMaxMemory() &&
          current.getMaxMemory() < e.getMaxMemory()) {
        result.set(e);
      }
    });
    return result.get();
  }
}
