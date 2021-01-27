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
package systems.reformcloud.reformcloud2.shared.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.Element;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Object;
import systems.reformcloud.reformcloud2.shared.collect.Entry2;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GsonObject extends GsonElement implements Object {

  final JsonObject gsonObject;

  public GsonObject(JsonObject gsonObject) {
    super(gsonObject);
    this.gsonObject = gsonObject;
  }

  @Override
  public @NotNull Optional<Element> remove(@NotNull String property) {
    JsonElement removed = this.gsonObject.remove(property);
    return Optional.ofNullable(removed == null ? null : ElementMapper.map(removed));
  }

  @Override
  public @NotNull Object add(@NotNull String property, @Nullable String value) {
    this.gsonObject.addProperty(property, value);
    return this;
  }

  @Override
  public @NotNull Object add(@NotNull String property, @Nullable Number value) {
    this.gsonObject.addProperty(property, value);
    return this;
  }

  @Override
  public @NotNull Object add(@NotNull String property, @Nullable Boolean value) {
    this.gsonObject.addProperty(property, value);
    return this;
  }

  @Override
  public @NotNull Object add(@NotNull String property, @Nullable Character value) {
    this.gsonObject.addProperty(property, value);
    return this;
  }

  @Override
  public @NotNull Object add(@NotNull String property, @Nullable Element value) {
    this.gsonObject.add(property, ElementMapper.map(value));
    return this;
  }

  @Override
  public @NotNull Optional<Element> get(@NotNull String property) {
    JsonElement element = this.gsonObject.get(property);
    return Optional.ofNullable(element == null ? null : ElementMapper.map(element));
  }

  @Override
  public boolean has(@NotNull String property) {
    return this.gsonObject.has(property);
  }

  @Override
  public @NotNull Set<Map.Entry<String, Element>> entrySet() {
    return this.gsonObject.entrySet().stream().map(entry -> {
      final Element mapped = ElementMapper.map(entry.getValue());
      return new Entry2<>(entry.getKey(), mapped);
    }).collect(Collectors.toSet());
  }

  @Override
  public @NotNull Object clone() {
    return new GsonObject(this.gsonObject.deepCopy());
  }
}
