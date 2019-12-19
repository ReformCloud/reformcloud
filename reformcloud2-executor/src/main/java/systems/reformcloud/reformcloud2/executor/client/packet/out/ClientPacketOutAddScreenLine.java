package systems.reformcloud.reformcloud2.executor.client.packet.out;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ClientPacketOutAddScreenLine extends DefaultPacket {

  public ClientPacketOutAddScreenLine(UUID uuid, String line) {
    super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 9,
          new JsonConfiguration().add("uuid", uuid).add("line", line));
  }
}
