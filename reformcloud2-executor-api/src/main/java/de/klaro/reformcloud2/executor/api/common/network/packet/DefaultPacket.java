package de.klaro.reformcloud2.executor.api.common.network.packet;

import de.klaro.reformcloud2.executor.api.common.configuration.Configurable;

import java.util.UUID;

public class DefaultPacket implements Packet {

    public DefaultPacket(int id, Configurable content) {
        this.id = id;
        this.content = content;
        this.uid = null;
    }

    public DefaultPacket(int id, Configurable content, UUID queryUniqueID) {
        this.id = id;
        this.content = content;
        this.uid = queryUniqueID;
    }

    private final int id;

    private UUID uid;

    private final Configurable content;

    @Override
    public int packetID() {
        return id;
    }

    @Override
    public UUID queryUniqueID() {
        return uid;
    }

    @Override
    public Configurable content() {
        return content;
    }

    @Override
    public void setQueryID(UUID id) {
        this.uid = id;
    }
}
