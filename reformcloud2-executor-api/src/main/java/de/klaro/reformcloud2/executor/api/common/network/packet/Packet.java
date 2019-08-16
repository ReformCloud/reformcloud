package de.klaro.reformcloud2.executor.api.common.network.packet;

import de.klaro.reformcloud2.executor.api.common.configuration.Configurable;

import java.util.UUID;

public interface Packet {

    int packetID();

    UUID queryUniqueID();

    Configurable content();

    void setQueryID(UUID id);
}
