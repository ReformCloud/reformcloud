package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutUnloadApplication extends DefaultPacket {

  public ExternalAPIPacketOutUnloadApplication(String application) {
    super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 2,
          new JsonConfiguration().add("app", application));
  }
}
