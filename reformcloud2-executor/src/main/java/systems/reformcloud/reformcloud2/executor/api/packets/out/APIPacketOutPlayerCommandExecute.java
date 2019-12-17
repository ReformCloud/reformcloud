package systems.reformcloud.reformcloud2.executor.api.packets.out;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class APIPacketOutPlayerCommandExecute extends DefaultPacket {

  public APIPacketOutPlayerCommandExecute(String name, UUID uuid,
                                          String message) {
    super(NetworkUtil.PLAYER_INFORMATION_BUS + 5, new JsonConfiguration()
                                                      .add("name", name)
                                                      .add("uuid", uuid)
                                                      .add("command", message));
  }
}
