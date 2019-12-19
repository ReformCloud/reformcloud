package systems.reformcloud.reformcloud2.executor.controller.packet.in.query;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public final class ControllerQueryStartProcess implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 31;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    String group = packet.content().getString("group");
    String template = packet.content().getString("template");
    JsonConfiguration extra = packet.content().get("extra");
    responses.accept(new DefaultPacket(
        -1, new JsonConfiguration().add(
                "result", ExecutorAPI.getInstance()
                              .getSyncAPI()
                              .getProcessSyncAPI()
                              .startProcess(group, template, extra))));
  }
}
