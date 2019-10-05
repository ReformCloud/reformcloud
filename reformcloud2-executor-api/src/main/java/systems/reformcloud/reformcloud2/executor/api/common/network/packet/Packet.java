package systems.reformcloud.reformcloud2.executor.api.common.network.packet;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import java.io.Serializable;
import java.util.UUID;

public interface Packet extends Serializable {

    int packetID();

    UUID queryUniqueID();

    JsonConfiguration content();

    void setQueryID(UUID id);

    void setContent(JsonConfiguration content);
}
