package systems.reformcloud.reformcloud2.executor.api.common.network.data;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.network.SerializableObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class ProtocolBuffer extends ByteBuf {

    public abstract void writeString(@Nullable String stringToWrite);

    public abstract void writeString(@Nullable String stringToWrite, int maxLength);

    @Nullable
    public abstract String readString();

    public abstract void writeArray(@NotNull byte[] bytes);

    public abstract void writeArray(@NotNull byte[] bytes, int limit);

    @NotNull
    public abstract byte[] readArray();

    @NotNull
    public abstract byte[] readArray(int limit);

    @NotNull
    public abstract byte[] toArray();

    public abstract void writeStringArray(@NotNull Collection<String> list);

    @NotNull
    public abstract List<String> readStringArray();

    public abstract void writeStringMap(@NotNull Map<String, String> list);

    @NotNull
    public abstract Map<String, String> readStringMap();

    public abstract void writeStringArrays(@NotNull String[] strings);

    @NotNull
    public abstract String[] readStringArrays();

    public abstract void writeLongArray(@NotNull long[] longs);

    @NotNull
    public abstract long[] readLongArray();

    public abstract <T extends SerializableObject> void writeObject(@Nullable T object);

    @Nullable
    public abstract <T extends SerializableObject> T readObject(@NotNull Class<T> tClass);

    public abstract <T extends SerializableObject> void writeObjects(@NotNull Collection<T> objects);

    @NotNull
    public abstract <T extends SerializableObject> List<T> readObjects(@NotNull Class<T> tClass);

    public abstract int readVarInt();

    public abstract void writeVarInt(int value);

    public abstract long readVarLong();

    public abstract void writeVarLong(long value);

    @Nullable
    public abstract UUID readUniqueId();

    public abstract void writeUniqueId(@Nullable UUID uniqueId);
}
