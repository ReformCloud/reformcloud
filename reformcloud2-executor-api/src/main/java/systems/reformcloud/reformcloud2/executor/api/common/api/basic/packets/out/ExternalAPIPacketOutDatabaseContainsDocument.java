package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutDatabaseContainsDocument
    extends DefaultPacket {

  public ExternalAPIPacketOutDatabaseContainsDocument(String table,
                                                      String key) {
    super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 15,
          new JsonConfiguration().add("table", table).add("key", key));
  }
}
