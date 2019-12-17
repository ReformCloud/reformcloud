package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.in.event;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalEventBusHandler;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public final class EventPacketInProcessClosed implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.EVENT_BUS + 1;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    ProcessInformation processInformation =
        packet.content().get("info", ProcessInformation.TYPE);
    ExternalEventBusHandler.getInstance().callEvent(
        new ProcessStoppedEvent(processInformation));
  }
}
