package systems.reformcloud.reformcloud2.executor.controller.packet.in.query;

import java.util.List;
import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public final class ControllerQueryGetProcesses implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 34;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    List<ProcessInformation> result;

    if (packet.content().has("group")) {
      String group = packet.content().getString("group");
      result = ExecutorAPI.getInstance()
                   .getSyncAPI()
                   .getProcessSyncAPI()
                   .getProcesses(group);
    } else {
      result = ExecutorAPI.getInstance()
                   .getSyncAPI()
                   .getProcessSyncAPI()
                   .getAllProcesses();
    }

    responses.accept(
        new DefaultPacket(-1, new JsonConfiguration().add("result", result)));
  }
}
