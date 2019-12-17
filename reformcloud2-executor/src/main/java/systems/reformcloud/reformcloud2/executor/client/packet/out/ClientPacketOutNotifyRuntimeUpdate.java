package systems.reformcloud.reformcloud2.executor.client.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ClientPacketOutNotifyRuntimeUpdate extends DefaultPacket {

  public ClientPacketOutNotifyRuntimeUpdate(
      DefaultClientRuntimeInformation clientRuntimeInformation) {
    super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 4,
          new JsonConfiguration().add("info", clientRuntimeInformation));
  }
}
