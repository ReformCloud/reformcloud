package systems.reformcloud.reformcloud2.executor.client.packet.out;

import java.util.Collection;
import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class ClientPacketOutScreenEnabled extends DefaultPacket {

  public ClientPacketOutScreenEnabled(UUID uniqueID, Collection<String> lines) {
    super(
        NetworkUtil.CONTROLLER_INFORMATION_BUS + 13,
        new JsonConfiguration().add("uniqueID", uniqueID).add("lines", lines));
  }
}
