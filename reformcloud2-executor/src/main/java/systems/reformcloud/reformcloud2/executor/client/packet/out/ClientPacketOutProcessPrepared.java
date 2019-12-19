package systems.reformcloud.reformcloud2.executor.client.packet.out;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ClientPacketOutProcessPrepared extends DefaultPacket {

  public ClientPacketOutProcessPrepared(String name, UUID uuid,
                                        String template) {
    super(NetworkUtil.NODE_TO_NODE_BUS + 5, new JsonConfiguration()
                                                .add("name", name)
                                                .add("uuid", uuid)
                                                .add("template", template));
  }
}
