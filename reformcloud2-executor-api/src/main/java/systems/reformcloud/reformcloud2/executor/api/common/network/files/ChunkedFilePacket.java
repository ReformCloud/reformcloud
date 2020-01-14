package systems.reformcloud.reformcloud2.executor.api.common.network.files;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class ChunkedFilePacket implements Packet {

    public ChunkedFilePacket(UUID uniqueID, String path, int readLength, byte[] current) {
        this.uniqueID = uniqueID;
        this.path = path;
        this.readLength = readLength;
        this.current = current;
    }

    private final UUID uniqueID;

    private final String path;

    private final int readLength;

    private final byte[] current;

    @Override
    public int packetID() {
        return NetworkUtil.FILE_BUS + 1;
    }

    @Nullable
    @Override
    public UUID queryUniqueID() {
        return null;
    }

    @Nonnull
    @Override
    public JsonConfiguration content() {
        return new JsonConfiguration();
    }

    @Nonnull
    @Override
    public byte[] extra() {
        return new byte[0];
    }

    @Override
    public void setQueryID(UUID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContent(JsonConfiguration content) {
        throw new UnsupportedOperationException();
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public String getPath() {
        return path;
    }

    public int getReadLength() {
        return readLength;
    }

    public byte[] getCurrent() {
        return current;
    }

    @Override
    public void write(@Nonnull ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeUTF(uniqueID.toString());
        objectOutputStream.writeUTF(path);
        objectOutputStream.writeInt(readLength);
        objectOutputStream.writeObject(current);
    }
}
