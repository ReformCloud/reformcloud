package de.klaro.reformcloud2.executor.api.common.network.packet;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import java.util.UUID;

public class DefaultPacket implements Packet {

    public DefaultPacket(int id, JsonConfiguration content) {
        this.id = id;
        this.content = content;
        this.uid = null;
    }

    public DefaultPacket(int id, JsonConfiguration content, UUID queryUniqueID) {
        this.id = id;
        this.content = content;
        this.uid = queryUniqueID;
    }

    private final int id;

    private UUID uid;

    private JsonConfiguration content;

    @Override
    public int packetID() {
        return id;
    }

    @Override
    public UUID queryUniqueID() {
        return uid;
    }

    @Override
    public JsonConfiguration content() {
        return content;
    }

    @Override
    public void setQueryID(UUID id) {
        this.uid = id;
    }

    @Override
    public void setContent(JsonConfiguration content) {
        this.content = content;
    }
}
