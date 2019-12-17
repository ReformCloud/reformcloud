package systems.reformcloud.reformcloud2.executor.node.network.packet.in;

import java.util.UUID;
import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

public class PacketInNodeQueueProcess implements NetworkHandler {

  @Override
  public int getHandlingPacketID() {
    return NetworkUtil.NODE_TO_NODE_BUS + 11;
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    ProcessGroup group = packet.content().get("group", ProcessGroup.TYPE);
    Template template = packet.content().get("template", Template.TYPE);
    JsonConfiguration data = packet.content().get("data");
    UUID uniqueID = packet.content().get("uuid", UUID.class);

    NodeExecutor.getInstance()
        .getNodeNetworkManager()
        .getNodeProcessHelper()
        .startLocalProcess(group, template, data, uniqueID);
  }
}
