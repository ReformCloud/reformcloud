package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutDatabaseRemoveDocument
    extends DefaultPacket {

  public ExternalAPIPacketOutDatabaseRemoveDocument(String table, String key,
                                                    boolean keyGiven) {
    super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 13,
          new JsonConfiguration()
              .add("table", table)
              .add("key", key)
              .add("action", keyGiven ? "remove_key" : "remove_identifier"));
  }
}
