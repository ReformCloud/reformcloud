package systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.out;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class ExternalAPIPacketOutDatabaseAction extends JsonPacket {

    public ExternalAPIPacketOutDatabaseAction(DatabaseAction action, String name) {
        super(ExternalAPIImplementation.EXTERNAL_PACKET_ID + 14, new JsonConfiguration()
                .add("action", action.key)
                .add("name", name)
        );
    }

    public enum DatabaseAction {

        CREATE("action_create"),

        DELETE("action_delete"),

        SIZE("action_size");

        DatabaseAction(String key) {
            this.key = key;
        }

        private final String key;
    }
}
