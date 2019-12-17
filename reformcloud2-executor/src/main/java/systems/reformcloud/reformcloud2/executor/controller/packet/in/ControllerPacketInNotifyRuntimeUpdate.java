package systems.reformcloud.reformcloud2.executor.controller.packet.in;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.controller.process.ClientManager;

public final class ControllerPacketInNotifyRuntimeUpdate
    implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.CONTROLLER_INFORMATION_BUS + 4;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    DefaultClientRuntimeInformation clientRuntimeInformation =
        packet.content().get("info", ClientRuntimeInformation.TYPE);
    ClientManager.INSTANCE.updateClient(clientRuntimeInformation);
  }
}
