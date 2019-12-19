package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutExecuteProcessCommand
    extends DefaultPacket {

  public ExternalAPIPacketOutExecuteProcessCommand(String process,
                                                   String command) {
    super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 35,
          new JsonConfiguration().add("process", process).add("cmd", command));
  }
}
