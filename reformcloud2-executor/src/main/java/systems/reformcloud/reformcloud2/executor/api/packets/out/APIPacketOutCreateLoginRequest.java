package systems.reformcloud.reformcloud2.executor.api.packets.out;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class APIPacketOutCreateLoginRequest extends DefaultPacket {

  public APIPacketOutCreateLoginRequest(UUID uniqueID, String name) {
    super(NetworkUtil.PLAYER_INFORMATION_BUS + 1,
          new JsonConfiguration().add("uniqueID", uniqueID).add("name", name));
  }
}
