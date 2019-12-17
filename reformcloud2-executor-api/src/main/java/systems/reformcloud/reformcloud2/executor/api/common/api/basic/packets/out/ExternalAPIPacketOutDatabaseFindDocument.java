package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public final class ExternalAPIPacketOutDatabaseFindDocument extends DefaultPacket {

    public ExternalAPIPacketOutDatabaseFindDocument(String table, String key, String identifier) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 10, new JsonConfiguration()
                .add("table", table)
                .add("key", key)
                .add("identifier", identifier)
        );
    }
}
