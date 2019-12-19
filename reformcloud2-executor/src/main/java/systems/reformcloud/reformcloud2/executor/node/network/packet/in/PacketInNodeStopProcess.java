package systems.reformcloud.reformcloud2.executor.node.network.packet.in;

import java.util.UUID;
import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.node.process.LocalNodeProcess;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

public class PacketInNodeStopProcess implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.NODE_TO_NODE_BUS + 1;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    UUID uniqueID = packet.content().get("uniqueID", UUID.class);
    LocalNodeProcess information =
        Links
            .filterToReference(
                LocalProcessManager.getNodeProcesses(),
                e
                -> e.getProcessInformation().getProcessUniqueID().equals(
                    uniqueID))
            .orNothing();

    if (information == null) {
      return;
    }

    information.shutdown();
  }
}
