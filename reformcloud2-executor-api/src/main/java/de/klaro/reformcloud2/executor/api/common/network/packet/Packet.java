package de.klaro.reformcloud2.executor.api.common.network.packet;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import java.util.UUID;

public interface Packet {

    int packetID();

    UUID queryUniqueID();

    JsonConfiguration content();

    void setQueryID(UUID id);
}
