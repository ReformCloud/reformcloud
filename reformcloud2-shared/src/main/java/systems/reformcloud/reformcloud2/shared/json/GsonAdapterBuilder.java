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

import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.JsonInstanceCreator;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.JsonReader;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.adapter.JsonAdapter;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.adapter.JsonAdapterBuilder;

import java.lang.reflect.Type;

public class GsonAdapterBuilder implements JsonAdapterBuilder {

  private final GsonBuilder gsonBuilder;

  public GsonAdapterBuilder() {
    this(new GsonBuilder());
  }

  private GsonAdapterBuilder(GsonBuilder gsonBuilder) {
    this.gsonBuilder = gsonBuilder;
  }

  @Override
  public @NotNull JsonAdapterBuilder enableNullSerialisation() {
    this.gsonBuilder.serializeNulls();
    return this;
  }

  @Override
  public @NotNull JsonAdapterBuilder enableComplexMapKeySerialization() {
    this.gsonBuilder.enableComplexMapKeySerialization();
    return this;
  }

  @Override
  public @NotNull JsonAdapterBuilder disableInnerClassSerialization() {
    this.gsonBuilder.disableInnerClassSerialization();
    return this;
  }

  @Override
  public @NotNull JsonAdapterBuilder enablePrettyPrinting() {
    this.gsonBuilder.setPrettyPrinting();
    return this;
  }

  @Override
  public @NotNull JsonAdapterBuilder enableLenient() {
    this.gsonBuilder.setLenient();
    return this;
  }

  @Override
  public @NotNull JsonAdapterBuilder disableHtmlEscaping() {
    this.gsonBuilder.disableHtmlEscaping();
    return this;
  }

  @Override
  public @NotNull JsonAdapterBuilder excludeModifiers(int... modifiers) {
    this.gsonBuilder.excludeFieldsWithModifiers(modifiers);
    return this;
  }

  @Override
  public @NotNull <T> JsonAdapterBuilder registerJsonReader(@NotNull Class<T> type, @NotNull JsonReader<T> reader) {
    this.gsonBuilder.registerTypeAdapter(type, new GsonSerializerBacking<>(reader));
    return this;
  }

  @Override
  public <T> @NotNull JsonAdapterBuilder registerJsonReader(@NotNull Type type, @NotNull JsonReader<T> reader) {
    this.gsonBuilder.registerTypeAdapter(type, new GsonSerializerBacking<>(reader));
    return this;
  }

  @Override
  public @NotNull <T> JsonAdapterBuilder registerInstanceCreator(@NotNull Class<T> type, @NotNull JsonInstanceCreator<T> instanceCreator) {
    this.gsonBuilder.registerTypeAdapter(type, new GsonInstanceCreatorBacking<>(instanceCreator));
    return this;
  }

  @Override
  public @NotNull <T> JsonAdapterBuilder registerInstanceCreator(@NotNull Type type, @NotNull JsonInstanceCreator<T> instanceCreator) {
    this.gsonBuilder.registerTypeAdapter(type, new GsonInstanceCreatorBacking<>(instanceCreator));
    return this;
  }

  @Override
  public @NotNull JsonAdapter build() {
    return new GsonAdapter(this.gsonBuilder.create());
  }

  @Override
  public @NotNull JsonAdapterBuilder clone() {
    return new GsonAdapterBuilder(this.gsonBuilder.create().newBuilder()); // looks like the only way to do that
  }

  private static final class GsonSerializerBacking<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private final JsonReader<T> backing;

    public GsonSerializerBacking(JsonReader<T> backing) {
      this.backing = backing;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return this.backing.deserialize(ElementMapper.map(json), typeOfT);
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
      return ElementMapper.map(this.backing.serialize(src, typeOfSrc));
    }
  }

  private static final class GsonInstanceCreatorBacking<T> implements InstanceCreator<T> {

    private final JsonInstanceCreator<T> instanceCreator;

    private GsonInstanceCreatorBacking(JsonInstanceCreator<T> instanceCreator) {
      this.instanceCreator = instanceCreator;
    }

    @Override
    public T createInstance(Type type) {
      return this.instanceCreator.createInstance(type);
    }
  }
}
