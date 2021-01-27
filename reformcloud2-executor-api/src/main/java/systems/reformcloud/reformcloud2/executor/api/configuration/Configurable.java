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
public interface Configurable<X, V extends Configurable<X, V>> extends Cloneable {

  @NotNull
  V add(@NotNull String key, @Nullable V value);

  @NotNull <T> V add(@NotNull String key, @Nullable T value);

  @NotNull
  V add(@NotNull String key, @Nullable String value);

  @NotNull
  V add(@NotNull String key, @Nullable Number value);

  @NotNull
  V add(@NotNull String key, @Nullable Boolean value);

  @NotNull
  V add(@NotNull String key, @Nullable Character value);

  @NotNull
  V remove(@NotNull String key);

  @NotNull
  V get(@NotNull String key);

  @Nullable <T> T get(@NotNull String key, @NotNull Class<T> type);

  @Nullable <T> T get(@NotNull String key, @NotNull Type type);

  @NotNull
  String getString(@NotNull String key);

  @NotNull
  Integer getInteger(@NotNull String key);

  @NotNull
  Long getLong(@NotNull String key);

  @NotNull
  Short getShort(@NotNull String key);

  @NotNull
  Byte getByte(@NotNull String key);

  @NotNull
  Double getDouble(@NotNull String key);

  @NotNull
  Float getFloat(@NotNull String key);

  @NotNull
  Boolean getBoolean(@NotNull String key);

  @NotNull
  Character getCharacter(@NotNull String key);

  V getOrDefault(@NotNull String key, V def);

  <T> T getOrDefault(@NotNull String key, Type type, T def);

  <T> T getOrDefault(@NotNull String key, Class<T> type, T def);

  String getOrDefault(@NotNull String key, String def);

  Integer getOrDefault(@NotNull String key, Integer def);

  Long getOrDefault(@NotNull String key, Long def);

  Short getOrDefault(@NotNull String key, Short def);

  Byte getOrDefault(@NotNull String key, Byte def);

  Boolean getOrDefault(@NotNull String key, Boolean def);

  Double getOrDefault(@NotNull String key, Double def);

  Float getOrDefault(@NotNull String key, Float def);

  Character getOrDefault(@NotNull String key, Character def);

  V getOrDefaultIf(@NotNull String key, V def, @NotNull Predicate<V> predicate);

  <T> T getOrDefaultIf(@NotNull String key, Type type, T def, @NotNull Predicate<T> predicate);

  <T> T getOrDefaultIf(@NotNull String key, Class<T> type, T def, @NotNull Predicate<T> predicate);

  String getOrDefaultIf(@NotNull String key, String def, @NotNull Predicate<String> predicate);

  Integer getOrDefaultIf(@NotNull String key, Integer def, @NotNull Predicate<Integer> predicate);

  Long getOrDefaultIf(@NotNull String key, Long def, @NotNull Predicate<Long> predicate);

  Short getOrDefaultIf(@NotNull String key, Short def, @NotNull Predicate<Short> predicate);

  Byte getOrDefaultIf(@NotNull String key, Byte def, @NotNull Predicate<Byte> predicate);

  Boolean getOrDefaultIf(@NotNull String key, Boolean def, @NotNull Predicate<Boolean> predicate);

  Double getOrDefaultIf(@NotNull String key, Double def, @NotNull Predicate<Double> predicate);

  Float getOrDefaultIf(@NotNull String key, Float def, @NotNull Predicate<Float> predicate);

  Character getOrDefaultIf(@NotNull String key, Character def, @NotNull Predicate<Character> predicate);

  boolean has(@NotNull String key);

  void write(@NotNull Path path);

  void write(@NotNull String path);

  void write(@NotNull File file);

  @NotNull
  String toPrettyString();

  byte[] toPrettyBytes();

  @NotNull
  Map<String, X> asMap();

  void clear();

  @NotNull
  V clone();
}
