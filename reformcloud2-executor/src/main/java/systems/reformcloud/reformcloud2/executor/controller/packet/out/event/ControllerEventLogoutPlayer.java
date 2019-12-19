package systems.reformcloud.reformcloud2.executor.controller.packet.out.event;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class ControllerEventLogoutPlayer extends DefaultPacket {

  public ControllerEventLogoutPlayer(String name, UUID uuid) {
    super(NetworkUtil.EVENT_BUS + 5,
          new JsonConfiguration().add("name", name).add("uuid", uuid));
  }
}
