package systems.reformcloud.reformcloud2.executor.controller.packet.in;

import java.util.UUID;
import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerLogoutEvent;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.join.OnlyProxyJoinHelper;
import systems.reformcloud.reformcloud2.executor.controller.ControllerExecutor;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.event.ControllerEventLogoutPlayer;

public final class ControllerPacketInAPILogoutPlayer implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.PLAYER_INFORMATION_BUS + 3;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    UUID uuid = packet.content().get("uuid", UUID.class);
    String name = packet.content().getString("name");

    OnlyProxyJoinHelper.onDisconnect(uuid);
    System.out.println(LanguageManager.get("player-logged-out", name, uuid,
                                           packetSender.getName()));

    ControllerExecutor.getInstance().getEventManager().callEvent(
        new PlayerLogoutEvent(name, uuid));
    ExecutorAPI.getInstance()
        .getSyncAPI()
        .getProcessSyncAPI()
        .getAllProcesses()
        .forEach(process
                 -> DefaultChannelManager.INSTANCE.get(process.getName())
                        .ifPresent(
                            channel
                            -> channel.sendPacket(
                                new ControllerEventLogoutPlayer(name, uuid))));
  }
}
