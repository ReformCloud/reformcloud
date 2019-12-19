package systems.reformcloud.reformcloud2.executor.api.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class APIBungeePacketOutRequestIngameMessages extends DefaultPacket {

  public APIBungeePacketOutRequestIngameMessages() {
    super(NetworkUtil.CONTROLLER_QUERY_BUS + 2, new JsonConfiguration());
  }
}
