package de.klaro.reformcloud2.executor.api.common.api.basic.packets.out;

import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutDatabaseContainsDocument extends DefaultPacket {

    public ExternalAPIPacketOutDatabaseContainsDocument(String table, String key) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 15, new JsonConfiguration()
                .add("table", table)
                .add("key", key)
        );
    }
}
