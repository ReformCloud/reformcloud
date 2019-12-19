package systems.reformcloud.reformcloud2.executor.client.packet.in;

import java.util.UUID;
import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.client.ClientExecutor;
import systems.reformcloud.reformcloud2.executor.client.screen.ProcessScreen;

public final class ClientPacketInToggleScreen implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.CONTROLLER_INFORMATION_BUS + 10;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    UUID uuid = packet.content().get("uuid", UUID.class);

    Links
        .filterToReference(ClientExecutor.getInstance()
                               .getScreenManager()
                               .getPerProcessScreenLines(),
                           uuid::equals)
        .ifPresent(ProcessScreen::toggleScreen);
  }
}
