package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutGetProcessGroups extends DefaultPacket {

  public ExternalAPIPacketOutGetProcessGroups() {
    super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 25,
          new JsonConfiguration());
  }
}
