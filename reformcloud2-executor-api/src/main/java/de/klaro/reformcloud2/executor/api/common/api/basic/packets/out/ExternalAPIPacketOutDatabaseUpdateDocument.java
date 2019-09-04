package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutDatabaseUpdateDocument extends DefaultPacket {

    public ExternalAPIPacketOutDatabaseUpdateDocument(String table, String key, JsonConfiguration newData, boolean keyGiven) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 12, new JsonConfiguration()
                .add("action", keyGiven ? "update_key" : "update_identifier")
                .add("table", table)
                .add("key", key)
                .add("data", newData)
        );
    }
}
