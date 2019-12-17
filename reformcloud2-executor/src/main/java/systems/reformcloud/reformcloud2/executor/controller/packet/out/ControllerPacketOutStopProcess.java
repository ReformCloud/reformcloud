package systems.reformcloud.reformcloud2.executor.controller.packet.out;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ControllerPacketOutStopProcess extends DefaultPacket {

  public ControllerPacketOutStopProcess(UUID uniqueID) {
    super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 3,
          new JsonConfiguration().add("uniqueID", uniqueID));
  }
}
