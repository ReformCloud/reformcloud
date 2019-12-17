package systems.reformcloud.reformcloud2.executor.controller.packet.out.event;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class ControllerEventPlayerServerSwitch extends DefaultPacket {

  public ControllerEventPlayerServerSwitch(UUID uuid, String target) {
    super(NetworkUtil.EVENT_BUS + 6,
          new JsonConfiguration().add("uuid", uuid).add("target", target));
  }
}
