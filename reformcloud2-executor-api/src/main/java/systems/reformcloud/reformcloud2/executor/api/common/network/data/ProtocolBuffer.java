package systems.reformcloud.reformcloud2.executor.api.common.network.data;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;

import java.util.List;
import java.util.UUID;

public abstract class ProtocolBuffer extends ByteBuf {

    public abstract void writeString(@NotNull String stringToWrite);

    public abstract void writeString(@NotNull String stringToWrite, int maxLength);

    @NotNull
    public abstract String readString();

    public abstract void writeArray(@NotNull byte[] bytes);

    public abstract void writeArray(@NotNull byte[] bytes, int limit);

    @NotNull
    public abstract byte[] readArray();

    @NotNull
    public abstract byte[] readArray(int limit);

    @NotNull
    public abstract byte[] toArray();

    public abstract void writeStringArray(@NotNull List<String> list);

    @NotNull
    public abstract List<String> readStringArray();

    public abstract <T extends SerializableObject> void writeObject(@NotNull T object);

    public abstract <T extends SerializableObject> T readObject(@NotNull Class<T> tClass);

    public abstract int readVarInt();

    public abstract void writeVarInt(int value);

    public abstract long readVarLong();

    public abstract void writeVarLong(long value);

    @NotNull
    public abstract UUID readUniqueId();

    public abstract void writeUniqueId(@NotNull UUID uniqueId);
}
