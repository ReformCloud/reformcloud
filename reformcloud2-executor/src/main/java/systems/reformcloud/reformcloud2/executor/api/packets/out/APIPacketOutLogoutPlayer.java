package systems.reformcloud.reformcloud2.executor.api.packets.out;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class APIPacketOutLogoutPlayer extends DefaultPacket {

  public APIPacketOutLogoutPlayer(UUID uuid, String name) {
    super(NetworkUtil.PLAYER_INFORMATION_BUS + 3,
          new JsonConfiguration().add("name", name).add("uuid", uuid));
  }
}
