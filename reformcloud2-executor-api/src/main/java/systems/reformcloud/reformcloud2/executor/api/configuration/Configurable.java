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
package systems.reformcloud.reformcloud2.executor.api.configuration;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Represents any configuration used in the cloud system
 */
public interface Configurable<X, V extends Configurable<X, V>> {

    @NotNull
    V add(@NotNull String key, @Nullable V value);

    @NotNull
    V add(@NotNull String key, @Nullable Object value);

    @NotNull
    V add(@NotNull String key, @Nullable String value);

    @NotNull
    V add(@NotNull String key, @Nullable Integer value);

    @NotNull
    V add(@NotNull String key, @Nullable Long value);

    @NotNull
    V add(@NotNull String key, @Nullable Short value);

    @NotNull
    V add(@NotNull String key, @Nullable Byte value);

    @NotNull
    V add(@NotNull String key, @Nullable Boolean value);

    @NotNull
    V add(@NotNull String key, @Nullable Double value);

    @NotNull
    V add(@NotNull String key, @Nullable Float value);

    @NotNull
    V remove(@NotNull String key);

    @NotNull
    V get(@NotNull String key);

    @Nullable <T> T get(@NotNull String key, @NotNull TypeToken<T> type);

    @Nullable <T> T get(@NotNull String key, @NotNull Class<T> type);

    @Nullable <T> T get(@NotNull String key, @NotNull Type type);

    @NotNull
    String getString(@NotNull String key);

    @NotNull
    Integer getInteger(String key);

    @NotNull
    Long getLong(String key);

    @NotNull
    Short getShort(String key);

    @NotNull
    Byte getByte(String key);

    @NotNull
    Boolean getBoolean(String key);

    @NotNull
    Double getDouble(String key);

    @NotNull
    Float getFloat(String key);

    V getOrDefault(String key, V def);

    <T> T getOrDefault(String key, Type type, T def);

    <T> T getOrDefault(String key, Class<T> type, T def);

    String getOrDefault(String key, String def);

    Integer getOrDefault(String key, Integer def);

    Long getOrDefault(String key, Long def);

    Short getOrDefault(String key, Short def);

    Byte getOrDefault(String key, Byte def);

    Boolean getOrDefault(String key, Boolean def);

    Double getOrDefault(String key, Double def);

    Float getOrDefault(String key, Float def);

    V getOrDefaultIf(String key, V def, Predicate<V> predicate);

    <T> T getOrDefaultIf(String key, Type type, T def, Predicate<T> predicate);

    <T> T getOrDefaultIf(String key, Class<T> type, T def, Predicate<T> predicate);

    String getOrDefaultIf(String key, String def, Predicate<String> predicate);

    Integer getOrDefaultIf(String key, Integer def, Predicate<Integer> predicate);

    Long getOrDefaultIf(String key, Long def, Predicate<Long> predicate);

    Short getOrDefaultIf(String key, Short def, Predicate<Short> predicate);

    Byte getOrDefaultIf(String key, Byte def, Predicate<Byte> predicate);

    Boolean getOrDefaultIf(String key, Boolean def, Predicate<Boolean> predicate);

    Double getOrDefaultIf(String key, Double def, Predicate<Double> predicate);

    Float getOrDefaultIf(String key, Float def, Predicate<Float> predicate);

    boolean has(String key);

    void write(Path path);

    void write(String path);

    void write(File path);

    @NotNull
    String toPrettyString();

    @NotNull
    byte[] toPrettyBytes();

    @NotNull
    Map<String, X> asMap();

    @NotNull
    V copy();
}
