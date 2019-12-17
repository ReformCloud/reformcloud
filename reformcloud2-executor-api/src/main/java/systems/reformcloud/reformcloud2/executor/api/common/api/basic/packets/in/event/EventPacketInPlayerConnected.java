package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.in.event;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.PlayerLoginEvent;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public class EventPacketInPlayerConnected implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.EVENT_BUS + 4;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    String name = packet.content().getString("name");

    ExternalEventBusHandler.getInstance().callEvent(new PlayerLoginEvent(name));
  }
}
