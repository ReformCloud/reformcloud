package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutGetInstalledPlugin
    extends DefaultPacket {

  public ExternalAPIPacketOutGetInstalledPlugin(String name, String process) {
    super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 29,
          new JsonConfiguration().add("name", name).add("process", process));
  }
}
