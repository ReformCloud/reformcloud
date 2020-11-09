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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProtocolBuffer {

    void writeString(@Nullable String stringToWrite);

    @Nullable String readString();

    void writeArray(@NotNull byte[] bytes);

    @NotNull byte[] readArray();

    @NotNull byte[] toByteArray();

    void writeStringArray(@NotNull Collection<String> list);

    @NotNull List<String> readStringArray();

    void writeStringMap(@NotNull Map<String, String> list);

    @NotNull Map<String, String> readStringMap();

    void writeStringArrays(@NotNull String[] strings);

    @NotNull String[] readStringArrays();

    void writeLongArray(@NotNull long[] longs);

    @NotNull long[] readLongArray();

    <T extends SerializableObject> void writeObject(@Nullable T object);

    @Nullable <T extends SerializableObject> T readObject(@NotNull Class<T> tClass);

    @Nullable <T extends SerializableObject, V extends T> T readObject(@NotNull Class<V> reader, @NotNull Class<T> type);

    <T extends SerializableObject> void writeObjects(@NotNull Collection<T> objects);

    @NotNull <T extends SerializableObject> List<T> readObjects(@NotNull Class<T> tClass);

    @NotNull <T extends SerializableObject, V extends T> List<T> readObjects(@NotNull Class<V> reader, @NotNull Class<T> type);

    int readVarInt();

    void writeVarInt(int value);

    @Nullable UUID readUniqueId();

    void writeUniqueId(@Nullable UUID uniqueId);

    void writeInteger(@Nullable Integer integer);

    @Nullable Integer readInteger();

    int readableBytes();

    boolean readBoolean();

    byte readByte();

    short readUnsignedByte();

    short readShort();

    int readUnsignedShort();

    int readMedium();

    int readUnsignedMedium();

    int readInt();

    long readUnsignedInt();

    long readLong();

    char readChar();

    float readFloat();

    double readDouble();

    void readBytes(byte[] target);

    void skipBytes(int amount);

    void writeBoolean(boolean b);

    void writeByte(int b);

    void writeShort(int s);

    void writeMedium(int medium);

    void writeInt(int i);

    void writeLong(long l);

    void writeChar(int c);

    void writeFloat(float f);

    void writeDouble(double d);

    void writeBytes(byte[] bytes);
}
