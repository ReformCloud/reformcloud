package systems.reformcloud.reformcloud2.executor.api.common.network.packet;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class JsonPacket implements Packet {

    public JsonPacket(int id, @Nonnull JsonConfiguration content) {
        this(id, content, null);
    }

    public JsonPacket(int id, @Nonnull JsonConfiguration content, @Nullable UUID queryUniqueID) {
        this(id, content, queryUniqueID, new byte[]{0});
    }

    public JsonPacket(int id, @Nonnull JsonConfiguration configuration, @Nullable UUID queryUniqueID, @Nonnull byte[] extra) {
        this.id = id;
        this.uid = queryUniqueID;
        this.content = configuration;
        this.extra = extra;
    }

    private int id;

    private UUID uid;

    private JsonConfiguration content;

    private byte[] extra;

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

    @Nonnull
    @Override
    public byte[] extra() {
        return extra;
    }

    @Override
    public void setQueryID(UUID id) {
        this.uid = id;
    }

    @Override
    public void setContent(JsonConfiguration content) {
        this.content = content;
    }

    @Override
    public void write(@Nonnull ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeUTF(uid == null ? "null" : uid.toString());
        objectOutputStream.writeObject(content.toPrettyBytes());
        objectOutputStream.writeObject(extra.length == 0 ? new byte[]{0} : extra);
    }
}
