package systems.reformcloud.reformcloud2.executor.controller.packet.in;

import java.util.UUID;
import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;

public final class ControllerPacketInProcessStopped implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.CONTROLLER_INFORMATION_BUS + 1;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    UUID uuid = packet.content().get("uuid", UUID.class);
    String name = packet.content().getString("name");

    System.out.println(LanguageManager.get("process-stopped", name, uuid,
                                           packetSender.getName()));

    ControllerExecutor.getInstance().getProcessManager().unregisterProcess(
        uuid);
  }
}
