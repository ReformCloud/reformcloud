/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.network.data;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.network.SerializableObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class ProtocolBuffer extends ByteBuf {

    public abstract void writeString(@Nullable String stringToWrite);

    @Nullable
    public abstract String readString();

    public abstract void writeArray(@NotNull byte[] bytes);

    @NotNull
    public abstract byte[] readArray();

    @NotNull
    public abstract byte[] toByteArray();

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

    @Nullable
    public abstract UUID readUniqueId();

    public abstract void writeUniqueId(@Nullable UUID uniqueId);

    public abstract void writeInteger(@Nullable Integer integer);

    @Nullable
    public abstract Integer readInteger();
}
