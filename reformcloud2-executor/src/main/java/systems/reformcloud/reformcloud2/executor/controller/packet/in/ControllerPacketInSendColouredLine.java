package systems.reformcloud.reformcloud2.executor.controller.packet.in;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public final class ControllerPacketInSendColouredLine
    implements NetworkHandler {
  @Override
  public int getHandlingPacketID() {
    return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 6;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    try {
      ExecutorAPI.getInstance()
          .getSyncAPI()
          .getConsoleSyncAPI()
          .sendColouredLine(packet.content().getString("line"));
    } catch (final IllegalAccessException ignored) {
      // Ignore this because it is not caused by a direct controller mistake
      // (but eg by developer not the owner)
    }
  }
}
