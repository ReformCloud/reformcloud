package systems.reformcloud.reformcloud2.executor.node.network.packet.in.api;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public final class PacketInDeleteProcessGroup implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 23;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    String name = packet.content().getString("name");
    ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().deleteProcessGroup(
        name);
  }
}
