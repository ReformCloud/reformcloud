package systems.reformcloud.reformcloud2.executor.controller.packet.in.query;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

public final class ControllerQueryInCreateProcessGroup
    implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 17;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    ProcessGroup group = packet.content().get("group", ProcessGroup.TYPE);
    responses.accept(new DefaultPacket(
        -1,
        new JsonConfiguration().add("result", ExecutorAPI.getInstance()
                                                  .getSyncAPI()
                                                  .getGroupSyncAPI()
                                                  .createProcessGroup(group))));
  }
}
