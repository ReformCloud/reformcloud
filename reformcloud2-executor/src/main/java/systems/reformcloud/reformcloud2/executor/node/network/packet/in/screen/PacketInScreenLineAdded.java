package systems.reformcloud.reformcloud2.executor.node.network.packet.in.screen;

import java.util.UUID;
import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public class PacketInScreenLineAdded implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.NODE_TO_NODE_BUS + 15;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    UUID uniqueID = packet.content().get("uniqueID", UUID.class);
    String line = packet.content().getString("line");

    ProcessInformation processInformation =
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(
            uniqueID);
    if (processInformation != null) {
      System.out.println(LanguageManager.get(
          "screen-line-added", processInformation.getName(), line));
    }
  }
}
