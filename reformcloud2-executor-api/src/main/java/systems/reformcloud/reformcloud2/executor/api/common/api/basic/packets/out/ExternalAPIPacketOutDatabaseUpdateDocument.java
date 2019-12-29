package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutDatabaseUpdateDocument extends JsonPacket {

    public ExternalAPIPacketOutDatabaseUpdateDocument(String table, String key, JsonConfiguration newData, boolean keyGiven) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 12, new JsonConfiguration()
                .add("action", keyGiven ? "update_key" : "update_identifier")
                .add("table", table)
                .add("key", key)
                .add("data", newData)
        );
    }
}
