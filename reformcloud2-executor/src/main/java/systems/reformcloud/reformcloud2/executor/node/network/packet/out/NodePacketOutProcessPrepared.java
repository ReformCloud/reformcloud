package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class NodePacketOutProcessPrepared extends DefaultPacket {

  public NodePacketOutProcessPrepared(String name, UUID uuid, String template) {
    super(NetworkUtil.NODE_TO_NODE_BUS + 10, new JsonConfiguration()
                                                 .add("name", name)
                                                 .add("uuid", uuid)
                                                 .add("template", template));
  }
}
