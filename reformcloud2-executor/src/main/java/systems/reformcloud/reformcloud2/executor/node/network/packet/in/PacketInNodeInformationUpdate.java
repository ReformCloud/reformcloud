package systems.reformcloud.reformcloud2.executor.node.network.packet.in;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

public class PacketInNodeInformationUpdate implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.NODE_TO_NODE_BUS + 9;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    NodeInformation information =
        packet.content().get("info", NodeInformation.TYPE);
    NodeExecutor.getInstance()
        .getClusterSyncManager()
        .handleNodeInformationUpdate(information);
  }
}
