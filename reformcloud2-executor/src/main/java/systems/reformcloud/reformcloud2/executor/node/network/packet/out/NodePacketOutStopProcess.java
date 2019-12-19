package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class NodePacketOutStopProcess extends DefaultPacket {

  public NodePacketOutStopProcess(UUID uniqueID) {
    super(NetworkUtil.NODE_TO_NODE_BUS + 1,
          new JsonConfiguration().add("uniqueID", uniqueID));
  }
}
