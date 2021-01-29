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
package systems.reformcloud.node.http;

import io.netty.handler.codec.http.HttpHeaders;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.http.Headers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DefaultHeaders implements Headers {

  private final HttpHeaders headers;

  public DefaultHeaders(HttpHeaders headers) {
    this.headers = headers;
  }

  @Override
  public @NotNull Optional<String> get(@NotNull String name) {
    return Optional.ofNullable(this.headers.get(name));
  }

  @Override
  public @NotNull String get(@NotNull String name, @NotNull String defaultValue) {
    return this.headers.get(name, defaultValue);
  }

  @Override
  public @NotNull Optional<Integer> getInt(@NotNull String name) {
    return Optional.ofNullable(this.headers.getInt(name));
  }

  @Override
  public int getInt(@NotNull String name, int defaultValue) {
    return this.headers.getInt(name, defaultValue);
  }

  @Override
  public @NotNull Optional<Short> getShort(@NotNull String name) {
    return Optional.ofNullable(this.headers.getShort(name));
  }

  @Override
  public short getShort(@NotNull String name, short defaultValue) {
    return this.headers.getShort(name, defaultValue);
  }

  @Override
  public @NotNull Optional<Long> getTimeMillis(@NotNull String name) {
    return Optional.ofNullable(this.headers.getTimeMillis(name));
  }

  @Override
  public long getTimeMillis(@NotNull String name, long defaultValue) {
    return this.headers.getTimeMillis(name, defaultValue);
  }

  @Override
  public @NotNull List<String> getAll(@NotNull String name) {
    return this.headers.getAll(name);
  }

  @Override
  public @NotNull List<Map.Entry<String, String>> entries() {
    return this.headers.entries();
  }

  @Override
  public boolean contains(@NotNull String name) {
    return this.headers.contains(name);
  }

  @Override
  public boolean isEmpty() {
    return this.headers.isEmpty();
  }

  @Override
  public int size() {
    return this.headers.size();
  }

  @Override
  public @NotNull Set<String> names() {
    return this.headers.names();
  }

  @Override
  public @NotNull Headers add(@NotNull Headers headers) {
    for (Map.Entry<String, String> entry : headers) {
      this.add(entry.getKey(), entry.getValue());
    }

    return this;
  }

  @Override
  public @NotNull Headers add(@NotNull String name, @NotNull Object value) {
    this.headers.add(name, value);
    return this;
  }

  @Override
  public @NotNull Headers addInt(@NotNull String name, int value) {
    this.headers.addInt(name, value);
    return this;
  }

  @Override
  public @NotNull Headers addShort(@NotNull String name, short value) {
    this.headers.addShort(name, value);
    return this;
  }

  @Override
  public @NotNull Headers setAll(@NotNull Headers headers) {
    for (Map.Entry<String, String> entry : headers) {
      this.set(entry.getKey(), entry.getValue());
    }

    return this;
  }

  @Override
  public @NotNull Headers set(@NotNull String name, @NotNull Object value) {
    this.headers.set(name, value);
    return this;
  }

  @Override
  public @NotNull Headers setInt(@NotNull String name, int value) {
    this.headers.setInt(name, value);
    return this;
  }

  @Override
  public @NotNull Headers setShort(@NotNull String name, short value) {
    this.headers.setShort(name, value);
    return this;
  }

  @Override
  public @NotNull Headers remove(@NotNull String name) {
    this.headers.remove(name);
    return this;
  }

  @Override
  public @NotNull Headers clear() {
    this.headers.clear();
    return this;
  }

  @NotNull
  @Override
  public Iterator<Map.Entry<String, String>> iterator() {
    return this.headers.iteratorAsString();
  }
}
