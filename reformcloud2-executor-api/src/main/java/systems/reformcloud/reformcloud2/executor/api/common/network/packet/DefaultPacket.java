package systems.reformcloud.reformcloud2.executor.api.common.network.packet;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import javax.annotation.Nonnull;
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

    @Nonnull
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
