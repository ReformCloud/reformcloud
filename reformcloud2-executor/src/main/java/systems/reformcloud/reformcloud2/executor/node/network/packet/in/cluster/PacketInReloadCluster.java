package systems.reformcloud.reformcloud2.executor.node.network.packet.in.cluster;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

public class PacketInReloadCluster implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.NODE_TO_NODE_BUS + 2;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    NodeExecutor.getInstance().getClusterSyncManager().handleClusterReload();
  }
}
