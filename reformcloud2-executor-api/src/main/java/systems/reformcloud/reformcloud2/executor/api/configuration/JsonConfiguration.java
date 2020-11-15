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
import systems.reformcloud.reformcloud2.executor.api.configuration.json.Element;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.JsonFactories;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.JsonParser;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.adapter.JsonAdapter;
import systems.reformcloud.reformcloud2.executor.api.configuration.json.types.Object;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class JsonConfiguration implements Configurable<Element, JsonConfiguration> {

    public static final JsonAdapter DEFAULT_ADAPTER = JsonAdapter.builder()
        .disableHtmlEscaping()
        .enablePrettyPrinting()
        .enableNullSerialisation()
        .build();
    protected static final Predicate<?> ALWAYS_TRUE = ignored -> true;
    protected static final Collector<Map.Entry<String, Element>, ?, Map<String, Element>> TO_MAP_COLLECTOR = Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);

    protected final Object json;
    protected final JsonAdapter adapter;

    protected JsonConfiguration() {
        this(JsonFactories.newObject());
    }

    protected JsonConfiguration(Object json) {
        this(json, DEFAULT_ADAPTER);
    }

    protected JsonConfiguration(JsonAdapter adapter) {
        this(JsonFactories.newObject(), adapter);
    }

    protected JsonConfiguration(Object json, JsonAdapter adapter) {
        this.json = json;
        this.adapter = adapter;
    }

    @NotNull
    public static JsonConfiguration newJsonConfiguration() {
        return new JsonConfiguration();
    }

    @NotNull
    public static JsonConfiguration newJsonConfiguration(@NotNull String json) {
        final Element backing = JsonParser.defaultParser().parse(json);
        if (backing instanceof Object) {
            return new JsonConfiguration((Object) backing);
        } else {
            return new JsonConfiguration();
        }
    }

    @NotNull
    public static JsonConfiguration newJsonConfiguration(@NotNull Object backingObject) {
        return new JsonConfiguration(backingObject);
    }

    @NotNull
    public static JsonConfiguration newJsonConfiguration(@NotNull JsonAdapter jsonAdapter) {
        return new JsonConfiguration(jsonAdapter);
    }

    @NotNull
    public static JsonConfiguration newJsonConfiguration(@NotNull Object backingObject, @NotNull JsonAdapter jsonAdapter) {
        return new JsonConfiguration(backingObject, jsonAdapter);
    }

    @NotNull
    public static JsonConfiguration newJsonConfiguration(@NotNull File file) {
        return newJsonConfiguration(file, DEFAULT_ADAPTER);
    }

    @NotNull
    public static JsonConfiguration newJsonConfiguration(@NotNull Path path) {
        return newJsonConfiguration(path, DEFAULT_ADAPTER);
    }

    @NotNull
    public static JsonConfiguration newJsonConfiguration(@NotNull String path, @NotNull JsonAdapter jsonAdapter) {
        return newJsonConfiguration(Path.of(path), jsonAdapter);
    }

    @NotNull
    public static JsonConfiguration newJsonConfiguration(@NotNull File file, @NotNull JsonAdapter jsonAdapter) {
        return newJsonConfiguration(file.toPath(), jsonAdapter);
    }

    @NotNull
    public static JsonConfiguration newJsonConfiguration(@NotNull Path path, @NotNull JsonAdapter jsonAdapter) {
        if (Files.exists(path)) {
            try (Reader reader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
                final Element backingElement = JsonParser.defaultParser().parse(reader);
                if (backingElement.isObject()) {
                    return newJsonConfiguration((Object) backingElement, jsonAdapter);
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return newJsonConfiguration(jsonAdapter);
    }

    @SuppressWarnings("unchecked")
    private static <T> Predicate<T> alwaysTrue() {
        return (Predicate<T>) ALWAYS_TRUE;
    }

    @Override
    public @NotNull JsonConfiguration add(@NotNull String key, @Nullable JsonConfiguration value) {
        if (value != null) {
            this.json.add(key, value.json);
        }
        return this;
    }

    @Override
    public <T> @NotNull JsonConfiguration add(@NotNull String key, @Nullable T value) {
        this.json.add(key, value == null ? JsonFactories.newNull() : this.adapter.toTree(value));
        return this;
    }

    @Override
    public @NotNull JsonConfiguration add(@NotNull String key, @Nullable String value) {
        this.json.add(key, value);
        return this;
    }

    @Override
    public @NotNull JsonConfiguration add(@NotNull String key, @Nullable Number value) {
        this.json.add(key, value);
        return this;
    }

    @Override
    public @NotNull JsonConfiguration add(@NotNull String key, @Nullable Boolean value) {
        this.json.add(key, value);
        return this;
    }

    @Override
    public @NotNull JsonConfiguration add(@NotNull String key, @Nullable Character value) {
        this.json.add(key, value);
        return this;
    }

    @Override
    public @NotNull JsonConfiguration remove(@NotNull String key) {
        this.json.remove(key);
        return this;
    }

    @Override
    public @NotNull JsonConfiguration get(@NotNull String key) {
        return this.getOrDefault(key, JsonConfiguration.newJsonConfiguration());
    }

    @Override
    public <T> @Nullable T get(@NotNull String key, @NotNull Class<T> type) {
        return this.getOrDefault(key, type, null);
    }

    @Override
    public <T> @Nullable T get(@NotNull String key, @NotNull Type type) {
        return this.getOrDefault(key, type, null);
    }

    @Override
    public @NotNull String getString(@NotNull String key) {
        return this.getOrDefault(key, "");
    }

    @Override
    public @NotNull Integer getInteger(@NotNull String key) {
        return this.getOrDefault(key, 0);
    }

    @Override
    public @NotNull Long getLong(@NotNull String key) {
        return this.getOrDefault(key, 0L);
    }

    @Override
    public @NotNull Short getShort(@NotNull String key) {
        return this.getOrDefault(key, (short) 0);
    }

    @Override
    public @NotNull Byte getByte(@NotNull String key) {
        return this.getOrDefault(key, (byte) 0);
    }

    @Override
    public @NotNull Double getDouble(@NotNull String key) {
        return this.getOrDefault(key, 0D);
    }

    @Override
    public @NotNull Float getFloat(@NotNull String key) {
        return this.getOrDefault(key, 0F);
    }

    @Override
    public @NotNull Boolean getBoolean(@NotNull String key) {
        return this.getOrDefault(key, false);
    }

    @Override
    public @NotNull Character getCharacter(@NotNull String key) {
        return this.getOrDefault(key, ' ');
    }

    @Override
    public JsonConfiguration getOrDefault(@NotNull String key, JsonConfiguration def) {
        return this.getOrDefaultIf(key, def, alwaysTrue());
    }

    @Override
    public <T> T getOrDefault(@NotNull String key, Type type, T def) {
        return this.getOrDefaultIf(key, type, def, alwaysTrue());
    }

    @Override
    public <T> T getOrDefault(@NotNull String key, Class<T> type, T def) {
        return this.getOrDefaultIf(key, type, def, alwaysTrue());
    }

    @Override
    public String getOrDefault(@NotNull String key, String def) {
        return this.getOrDefaultIf(key, def, alwaysTrue());
    }

    @Override
    public Integer getOrDefault(@NotNull String key, Integer def) {
        return this.getOrDefaultIf(key, def, alwaysTrue());
    }

    @Override
    public Long getOrDefault(@NotNull String key, Long def) {
        return this.getOrDefaultIf(key, def, alwaysTrue());
    }

    @Override
    public Short getOrDefault(@NotNull String key, Short def) {
        return this.getOrDefaultIf(key, def, alwaysTrue());
    }

    @Override
    public Byte getOrDefault(@NotNull String key, Byte def) {
        return this.getOrDefaultIf(key, def, alwaysTrue());
    }

    @Override
    public Boolean getOrDefault(@NotNull String key, Boolean def) {
        return this.getOrDefaultIf(key, def, alwaysTrue());
    }

    @Override
    public Double getOrDefault(@NotNull String key, Double def) {
        return this.getOrDefaultIf(key, def, alwaysTrue());
    }

    @Override
    public Float getOrDefault(@NotNull String key, Float def) {
        return this.getOrDefaultIf(key, def, alwaysTrue());
    }

    @Override
    public Character getOrDefault(@NotNull String key, Character def) {
        return this.getOrDefaultIf(key, def, alwaysTrue());
    }

    @Override
    public JsonConfiguration getOrDefaultIf(@NotNull String key, JsonConfiguration def, @NotNull Predicate<JsonConfiguration> predicate) {
        return this.getInternal(key).map(element -> {
            if (element instanceof Object) {
                final JsonConfiguration config = JsonConfiguration.newJsonConfiguration((Object) element, this.adapter);
                if (predicate.test(config)) {
                    return config;
                }
            }
            return def;
        }).orElse(def);
    }

    @Override
    public <T> T getOrDefaultIf(@NotNull String key, Type type, T def, @NotNull Predicate<T> predicate) {
        return this.getInternal(key).map(element -> {
            if (element instanceof Object) {
                final T result = this.adapter.fromJson(element, type);
                if (predicate.test(result)) {
                    return result;
                }
            }
            return def;
        }).orElse(def);
    }

    @Override
    public <T> T getOrDefaultIf(@NotNull String key, Class<T> type, T def, @NotNull Predicate<T> predicate) {
        return this.getOrDefaultIf(key, (Type) type, def, predicate);
    }

    @Override
    public String getOrDefaultIf(@NotNull String key, String def, @NotNull Predicate<String> predicate) {
        return this.json.get(key).map(element -> {
            final String result = element.getAsString();
            if (predicate.test(result)) {
                return result;
            }
            return def;
        }).orElse(def);
    }

    @Override
    public Integer getOrDefaultIf(@NotNull String key, Integer def, @NotNull Predicate<Integer> predicate) {
        return this.json.get(key).map(element -> {
            final int result = element.getAsInt();
            if (predicate.test(result)) {
                return result;
            }
            return def;
        }).orElse(def);
    }

    @Override
    public Long getOrDefaultIf(@NotNull String key, Long def, @NotNull Predicate<Long> predicate) {
        return this.json.get(key).map(element -> {
            final long result = element.getAsLong();
            if (predicate.test(result)) {
                return result;
            }
            return def;
        }).orElse(def);
    }

    @Override
    public Short getOrDefaultIf(@NotNull String key, Short def, @NotNull Predicate<Short> predicate) {
        return this.json.get(key).map(element -> {
            final short result = element.getAsShort();
            if (predicate.test(result)) {
                return result;
            }
            return def;
        }).orElse(def);
    }

    @Override
    public Byte getOrDefaultIf(@NotNull String key, Byte def, @NotNull Predicate<Byte> predicate) {
        return this.json.get(key).map(element -> {
            final byte result = element.getAsByte();
            if (predicate.test(result)) {
                return result;
            }
            return def;
        }).orElse(def);
    }

    @Override
    public Boolean getOrDefaultIf(@NotNull String key, Boolean def, @NotNull Predicate<Boolean> predicate) {
        return this.json.get(key).map(element -> {
            final boolean result = element.getAsBoolean();
            if (predicate.test(result)) {
                return result;
            }
            return def;
        }).orElse(def);
    }

    @Override
    public Double getOrDefaultIf(@NotNull String key, Double def, @NotNull Predicate<Double> predicate) {
        return this.json.get(key).map(element -> {
            final double result = element.getAsDouble();
            if (predicate.test(result)) {
                return result;
            }
            return def;
        }).orElse(def);
    }

    @Override
    public Float getOrDefaultIf(@NotNull String key, Float def, @NotNull Predicate<Float> predicate) {
        return this.json.get(key).map(element -> {
            final float result = element.getAsFloat();
            if (predicate.test(result)) {
                return result;
            }
            return def;
        }).orElse(def);
    }

    @Override
    public Character getOrDefaultIf(@NotNull String key, Character def, @NotNull Predicate<Character> predicate) {
        return this.json.get(key).map(element -> {
            final char result = element.getAsString().charAt(0);
            if (predicate.test(result)) {
                return result;
            }
            return def;
        }).orElse(def);
    }

    @Override
    public boolean has(@NotNull String key) {
        return this.json.has(key);
    }

    @Override
    public void write(@NotNull Path path) {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(path, StandardOpenOption.CREATE))) {
            this.adapter.toJson(this.json, writer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void write(@NotNull String path) {
        this.write(Path.of(path));
    }

    @Override
    public void write(@NotNull File file) {
        this.write(file.toPath());
    }

    @Override
    public @NotNull String toPrettyString() {
        return this.adapter.toJson(this.json);
    }

    @Override
    public @NotNull byte[] toPrettyBytes() {
        return this.toPrettyString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public @NotNull Map<String, Element> asMap() {
        return this.json.entrySet().stream().collect(TO_MAP_COLLECTOR);
    }

    @Override
    public void clear() {
        for (Map.Entry<String, Element> entry : this.json.entrySet()) {
            this.json.remove(entry.getKey());
        }
    }

    @Override
    public @NotNull JsonConfiguration clone() {
        return JsonConfiguration.newJsonConfiguration(this.json.clone(), this.adapter);
    }

    @NotNull
    private Optional<Element> getInternal(@NotNull String key) {
        return this.json.get(key);
    }

    @NotNull
    public Object getBackingObject() {
        return this.json;
    }

    @NotNull
    public JsonAdapter getJsonAdapter() {
        return this.adapter;
    }
}
