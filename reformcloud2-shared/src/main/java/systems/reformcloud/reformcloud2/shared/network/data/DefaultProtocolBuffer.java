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
package systems.reformcloud.reformcloud2.shared.network.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.enums.EnumUtil;
import systems.reformcloud.reformcloud2.executor.api.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.network.data.SerializableObject;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultProtocolBuffer implements ProtocolBuffer {

    private final ByteBuf wrapped;

    public DefaultProtocolBuffer(@NotNull ByteBuf wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void writeString(@Nullable String stringToWrite) {
        this.writeBoolean(stringToWrite == null);
        if (stringToWrite == null) {
            return;
        }

        byte[] bytes = stringToWrite.getBytes(StandardCharsets.UTF_8);
        this.writeVarInt(bytes.length);
        this.writeBytes(bytes);
    }

    @Override
    @Nullable
    public String readString() {
        if (this.readBoolean()) {
            return null;
        }

        int length = this.readVarInt();

        byte[] bytes = new byte[length];
        this.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void writeArray(@NotNull byte[] bytes) {
        this.writeVarInt(bytes.length);
        this.writeBytes(bytes);
    }

    @Override
    @NotNull
    public byte[] readArray() {
        byte[] bytes = new byte[this.readVarInt()];
        this.readBytes(bytes);
        return bytes;
    }

    @Override
    @NotNull
    public byte[] toByteArray() {
        byte[] bytes = new byte[this.readableBytes()];
        this.readBytes(bytes);
        return bytes;
    }

    @Override
    public void writeStringArray(@NotNull Collection<String> list) {
        this.writeVarInt(list.size());
        for (String s : list) {
            this.writeString(s);
        }
    }

    @Override
    @NotNull
    public List<String> readStringArray() {
        int length = this.readVarInt();
        List<String> out = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            out.add(this.readString());
        }

        return out;
    }

    @Override
    public void writeStringMap(@NotNull Map<String, String> list) {
        this.writeVarInt(list.size());
        for (Map.Entry<String, String> stringStringEntry : list.entrySet()) {
            this.writeString(stringStringEntry.getKey());
            this.writeString(stringStringEntry.getValue());
        }
    }

    @NotNull
    @Override
    public Map<String, String> readStringMap() {
        int size = this.readVarInt();
        Map<String, String> out = new ConcurrentHashMap<>(size);

        for (int i = 0; i < size; i++) {
            out.put(this.readString(), this.readString());
        }

        return out;
    }

    @Override
    public void writeStringArrays(@NotNull String[] strings) {
        this.writeVarInt(strings.length);
        for (String string : strings) {
            this.writeString(string);
        }
    }

    @NotNull
    @Override
    public String[] readStringArrays() {
        int size = this.readVarInt();
        String[] strings = new String[size];

        for (int i = 0; i < size; i++) {
            strings[i] = this.readString();
        }

        return strings;
    }

    @Override
    public void writeLongArray(@NotNull long[] longs) {
        this.writeVarInt(longs.length);
        for (long aLong : longs) {
            this.writeLong(aLong);
        }
    }

    @NotNull
    @Override
    public long[] readLongArray() {
        int size = this.readVarInt();
        long[] out = new long[size];

        for (int i = 0; i < size; i++) {
            out[i] = this.readLong();
        }

        return out;
    }

    @Override
    public <T extends SerializableObject> void writeObject(@Nullable T object) {
        this.writeBoolean(object == null);
        if (object == null) {
            return;
        }

        object.write(this);
    }

    @Override
    public <T extends SerializableObject> T readObject(@NotNull Class<T> tClass) {
        if (this.readBoolean()) {
            return null;
        }

        return this.readObject0(tClass);
    }

    @Override
    public <T extends SerializableObject, V extends T> @Nullable T readObject(@NotNull Class<V> reader, @NotNull Class<T> type) {
        if (this.readBoolean()) {
            return null;
        } else {
            return this.readObject(reader);
        }
    }

    @Nullable
    private <T extends SerializableObject> T readObject0(@NotNull Class<T> tClass) {
        try {
            T instance = tClass.getDeclaredConstructor().newInstance();
            instance.read(this);
            return instance;
        } catch (final NoSuchMethodException exception) {
            System.err.println("Unable to find NoArgsConstructor for object class " + tClass.getName());
        } catch (final IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            System.err.println("An exception occurred while reading object class " + tClass.getName());
            exception.printStackTrace();
        }

        return null;
    }

    @Override
    public <T extends SerializableObject> void writeObjects(@NotNull Collection<T> objects) {
        this.writeVarInt(objects.size());
        for (T object : objects) {
            object.write(this);
        }
    }

    @NotNull
    @Override
    public <T extends SerializableObject> List<T> readObjects(@NotNull Class<T> tClass) {
        List<T> out = new CopyOnWriteArrayList<>();
        int initial = this.readVarInt();
        for (int i = 0; i < initial; i++) {
            out.add(this.readObject0(tClass));
        }

        return out;
    }

    @Override
    public @NotNull <T extends SerializableObject, V extends T> List<T> readObjects(@NotNull Class<V> reader, @NotNull Class<T> type) {
        List<T> out = new CopyOnWriteArrayList<>();
        int initial = this.readVarInt();
        for (int i = 0; i < initial; i++) {
            out.add(this.readObject0(reader));
        }

        return out;
    }

    @Override
    public int readVarInt() {
        int i = 0;
        int maxRead = Math.min(5, this.readableBytes());
        for (int j = 0; j < maxRead; j++) {
            int k = this.readByte();
            i |= (k & 127) << j * 7;
            if ((k & 128) != 128) {
                return i;
            }
        }

        throw new IllegalStateException("Bad VarInt received " + ByteBufUtil.hexDump(this.wrapped));
    }

    @Override
    public void writeVarInt(int value) {
        while (true) {
            if ((value & -128) == 0) {
                this.writeByte(value);
                return;
            }

            this.writeByte(value & 127 | 128);
            value >>>= 7;
        }
    }

    @Override
    @Nullable
    public UUID readUniqueId() {
        if (this.readBoolean()) {
            return null;
        }

        return new UUID(this.readLong(), this.readLong());
    }

    @Override
    public void writeUniqueId(@Nullable UUID uniqueId) {
        this.writeBoolean(uniqueId == null);
        if (uniqueId == null) {
            return;
        }

        this.writeLong(uniqueId.getMostSignificantBits());
        this.writeLong(uniqueId.getLeastSignificantBits());
    }

    @Override
    public void writeInteger(@Nullable Integer integer) {
        this.writeBoolean(integer == null);
        if (integer == null) {
            return;
        }

        this.writeInt(integer);
    }

    @Nullable
    @Override
    public Integer readInteger() {
        if (this.readBoolean()) {
            return null;
        }

        return this.readInt();
    }

    @Override
    public int readableBytes() {
        return this.wrapped.readableBytes();
    }

    @Override
    public boolean readBoolean() {
        return this.wrapped.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.wrapped.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.wrapped.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.wrapped.readShort();
    }

    @Override
    public int readUnsignedShort() {
        return this.wrapped.readUnsignedShort();
    }

    @Override
    public int readMedium() {
        return this.wrapped.readMedium();
    }

    @Override
    public int readUnsignedMedium() {
        return this.wrapped.readUnsignedMedium();
    }

    @Override
    public int readInt() {
        return this.wrapped.readInt();
    }

    @Override
    public long readUnsignedInt() {
        return this.wrapped.readUnsignedInt();
    }

    @Override
    public long readLong() {
        return this.wrapped.readLong();
    }

    @Override
    public char readChar() {
        return this.wrapped.readChar();
    }

    @Override
    public float readFloat() {
        return this.wrapped.readFloat();
    }

    @Override
    public double readDouble() {
        return this.wrapped.readDouble();
    }

    @Override
    public <E extends Enum<E>> E readEnum(@NotNull Class<E> enumClass) {
        return EnumUtil.findEnumFieldByIndex(enumClass, this.readVarInt()).orElseThrow();
    }

    @Override
    public void readBytes(byte[] target) {
        this.wrapped.readBytes(target);
    }

    @Override
    public void skipBytes(int amount) {
        this.wrapped.skipBytes(amount);
    }

    @Override
    public void writeBoolean(boolean b) {
        this.wrapped.writeBoolean(b);
    }

    @Override
    public void writeByte(int b) {
        this.wrapped.writeByte(b);
    }

    @Override
    public void writeShort(int s) {
        this.wrapped.writeShort(s);
    }

    @Override
    public void writeMedium(int medium) {
        this.wrapped.writeMedium(medium);
    }

    @Override
    public void writeInt(int i) {
        this.wrapped.writeInt(i);
    }

    @Override
    public void writeLong(long l) {
        this.wrapped.writeLong(l);
    }

    @Override
    public void writeChar(int c) {
        this.wrapped.writeChar(c);
    }

    @Override
    public void writeFloat(float f) {
        this.wrapped.writeFloat(f);
    }

    @Override
    public void writeDouble(double d) {
        this.wrapped.writeDouble(d);
    }

    @Override
    public void writeBytes(byte[] bytes) {
        this.wrapped.writeBytes(bytes);
    }

    @Override
    public void writeEnum(@NotNull Enum<?> constant) {
        this.writeVarInt(constant.ordinal());
    }
}
