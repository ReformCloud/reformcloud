package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutDatabaseInsertDocument extends DefaultPacket {

    public ExternalAPIPacketOutDatabaseInsertDocument(String table, String key, String identifier, JsonConfiguration data) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 11, new JsonConfiguration()
                .add("table", table)
                .add("key", key)
                .add("identifier", identifier)
                .add("data", data)
        );
    }
}
