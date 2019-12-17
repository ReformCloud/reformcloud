package systems.reformcloud.reformcloud2.executor.node.network.packet.out.screen;

import java.util.Collection;
import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class NodePacketOutScreenEnabled extends DefaultPacket {

  public NodePacketOutScreenEnabled(UUID uniqueID, Collection<String> lines) {
    super(
        NetworkUtil.NODE_TO_NODE_BUS + 16,
        new JsonConfiguration().add("uniqueID", uniqueID).add("lines", lines));
  }
}
