package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import java.util.UUID;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutGetProcess extends DefaultPacket {

  public ExternalAPIPacketOutGetProcess(String name) {
    super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 33,
          new JsonConfiguration().add("name", name));
  }

  public ExternalAPIPacketOutGetProcess(UUID uniqueID) {
    super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 33,
          new JsonConfiguration().add("uniqueID", uniqueID));
  }
}
