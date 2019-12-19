package systems.reformcloud.reformcloud2.executor.node.network.packet.in.api;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public final class PacketInExecuteProcessCommand implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 35;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    String command = packet.content().getString("cmd");
    String process = packet.content().getString("process");
    ExecutorAPI.getInstance()
        .getSyncAPI()
        .getProcessSyncAPI()
        .executeProcessCommand(process, command);
  }
}
