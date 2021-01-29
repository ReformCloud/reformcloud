/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.configuration.data;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.network.data.SerializableObject;

import java.util.Optional;

public interface JsonDataHolder<T extends JsonDataHolder<T>> extends SerializableObject, Cloneable {

  @NotNull <V> Optional<V> get(@NotNull String key, @NotNull Class<V> type);

  @NotNull <V> T add(@NotNull String key, @NotNull V value);

  @NotNull <V> T set(@NotNull String key, @NotNull V value);

  @NotNull
  T remove(@NotNull String key);

  boolean has(@NotNull String key);

  @NotNull
  JsonConfiguration getData();

  void setData(@NotNull JsonConfiguration data);

  void clearData();

  @NotNull
  T clone();
}
