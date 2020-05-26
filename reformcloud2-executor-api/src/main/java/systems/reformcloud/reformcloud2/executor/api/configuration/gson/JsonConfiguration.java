/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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

import com.google.gson.*;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.InternalJsonParser;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfigurationTypeAdapter;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class JsonConfiguration implements Configurable<JsonElement, JsonConfiguration> {

    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .serializeSpecialFloatingPointValues()
            .setDateFormat(DateFormat.LONG)
            .registerTypeAdapterFactory(TypeAdapters.newTypeHierarchyFactory(JsonConfiguration.class, new JsonConfigurationTypeAdapter()))
            .create();
    private JsonObject jsonObject = new JsonObject();

    public JsonConfiguration() {
    }

    public JsonConfiguration(@NotNull Gson gson) {
        this.gson = gson;
    }

    public JsonConfiguration(@NotNull byte[] bytes) {
        try (InputStreamReader stream = new InputStreamReader(new ByteArrayInputStream(bytes))) {
            JsonElement element = InternalJsonParser.parseReader(stream);
            if (!element.isJsonObject()) {
                this.jsonObject = new JsonObject();
                return;
            }

            this.jsonObject = element.getAsJsonObject();
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
            this.jsonObject = new JsonObject();
        }
    }

    public JsonConfiguration(String json) {
        JsonElement jsonElement;
        try {
            jsonElement = InternalJsonParser.parseString(json);
        } catch (final Exception ex) {
            jsonElement = new JsonObject();
        }

        Conditions.isTrue(jsonElement.isJsonObject(), "JsonElement has to be a json object");
        this.jsonObject = jsonElement.getAsJsonObject();
    }

    public JsonConfiguration(InputStream stream) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonElement jsonElement;
            try {
                jsonElement = InternalJsonParser.parseReader(inputStreamReader);
            } catch (final Exception ex) {
                jsonElement = new JsonObject();
            }

            Conditions.isTrue(jsonElement.isJsonObject(), "JsonElement has to be a json object");
            this.jsonObject = jsonElement.getAsJsonObject();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    public JsonConfiguration(Reader json) {
        JsonElement jsonElement;
        try {
            jsonElement = InternalJsonParser.parseReader(json);
        } catch (final Exception ex) {
            jsonElement = new JsonObject();
        }

        Conditions.isTrue(jsonElement.isJsonObject(), "JsonElement has to be a json object");
        this.jsonObject = jsonElement.getAsJsonObject();
    }

    public JsonConfiguration(File file) {
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            this.jsonObject = new JsonConfiguration(stream).getJsonObject();
        } catch (final IOException ex) {
            ex.printStackTrace();
            this.jsonObject = new JsonObject();
        }
    }

    public JsonConfiguration(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public static JsonConfiguration read(Path path) {
        if (!Files.exists(path)) {
            return new JsonConfiguration();
        }

        try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8)) {
            return new JsonConfiguration(inputStreamReader);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return new JsonConfiguration();
    }

    @NotNull
    public static JsonConfiguration fromMap(@NotNull Map<String, JsonElement> map) {
        JsonConfiguration out = new JsonConfiguration();

        for (Map.Entry<String, JsonElement> stringJsonElementEntry : map.entrySet()) {
            out.getJsonObject().add(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue());
        }

        return out;
    }

    public static JsonConfiguration read(String path) {
        return read(Paths.get(path));
    }

    public static JsonConfiguration read(File path) {
        return read(path.toPath());
    }

    @NotNull
    @Override
    public JsonConfiguration add(@NotNull String key, @Nullable JsonConfiguration value) {
        if (value == null) {
            this.jsonObject.add(key, JsonNull.INSTANCE);
            return this;
        }

        this.jsonObject.add(key, value.jsonObject);
        return this;
    }

    @NotNull
    @Override
    public JsonConfiguration add(@NotNull String key, @Nullable Object value) {
        if (value == null) {
            this.jsonObject.add(key, JsonNull.INSTANCE);
            return this;
        }

        this.jsonObject.add(key, this.gson.toJsonTree(value));
        return this;
    }

    @NotNull
    @Override
    public JsonConfiguration add(@NotNull String key, @Nullable String value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    @NotNull
    @Override
    public JsonConfiguration add(@NotNull String key, @Nullable Integer value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    @NotNull
    @Override
    public JsonConfiguration add(@NotNull String key, @Nullable Long value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    @NotNull
    @Override
    public JsonConfiguration add(@NotNull String key, @Nullable Short value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    @NotNull
    @Override
    public JsonConfiguration add(@NotNull String key, @Nullable Byte value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    @NotNull
    @Override
    public JsonConfiguration add(@NotNull String key, @Nullable Boolean value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    @NotNull
    @Override
    public JsonConfiguration add(@NotNull String key, @Nullable Double value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    @NotNull
    @Override
    public JsonConfiguration add(@NotNull String key, @Nullable Float value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    @NotNull
    @Override
    public JsonConfiguration remove(@NotNull String key) {
        this.jsonObject.remove(key);
        return this;
    }

    @NotNull
    @Override
    public JsonConfiguration get(@NotNull String key) {
        return this.getOrDefault(key, new JsonConfiguration());
    }

    @Override
    public <T> T get(@NotNull String key, @NotNull TypeToken<T> type) {
        return this.getOrDefault(key, type.getType(), null);
    }

    @Override
    public <T> T get(@NotNull String key, @NotNull Class<T> type) {
        return this.getOrDefault(key, type, null);
    }

    @NotNull
    @Override
    public String getString(@NotNull String key) {
        return this.getOrDefault(key, "");
    }

    @NotNull
    @Override
    public Integer getInteger(String key) {
        return this.getOrDefault(key, -1);
    }

    @NotNull
    @Override
    public Long getLong(String key) {
        return this.getOrDefault(key, (long) -1);
    }

    @NotNull
    @Override
    public Short getShort(String key) {
        return this.getOrDefault(key, (short) -1);
    }

    @NotNull
    @Override
    public Byte getByte(String key) {
        return this.getOrDefault(key, (byte) -1);
    }

    @NotNull
    @Override
    public Boolean getBoolean(String key) {
        return this.getOrDefault(key, false);
    }

    @NotNull
    @Override
    public Double getDouble(String key) {
        return this.getOrDefault(key, -1D);
    }

    @NotNull
    @Override
    public Float getFloat(String key) {
        return this.getOrDefault(key, -1F);
    }

    @Override
    public JsonConfiguration getOrDefault(String key, JsonConfiguration def) {
        return this.getOrDefaultIf(key, def, jsonConfiguration -> true);
    }

    @Override
    public <T> T getOrDefault(String key, Type type, T def) {
        return this.getOrDefaultIf(key, type, def, t -> true);
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> type, T def) {
        return this.getOrDefaultIf(key, type, def, t -> true);
    }

    @Override
    public String getOrDefault(String key, String def) {
        return this.getOrDefaultIf(key, def, s -> true);
    }

    @Override
    public Integer getOrDefault(String key, Integer def) {
        return this.getOrDefaultIf(key, def, integer -> true);
    }

    @Override
    public Long getOrDefault(String key, Long def) {
        return this.getOrDefaultIf(key, def, aLong -> true);
    }

    @Override
    public Short getOrDefault(String key, Short def) {
        return this.getOrDefaultIf(key, def, aShort -> true);
    }

    @Override
    public Byte getOrDefault(String key, Byte def) {
        return this.getOrDefaultIf(key, def, aByte -> true);
    }

    @Override
    public Boolean getOrDefault(String key, Boolean def) {
        return this.getOrDefaultIf(key, def, aBoolean -> true);
    }

    @Override
    public Double getOrDefault(String key, Double def) {
        return this.getOrDefaultIf(key, def, s -> true);
    }

    @Override
    public Float getOrDefault(String key, Float def) {
        return this.getOrDefaultIf(key, def, aFloat -> true);
    }

    private JsonElement getElement(String key) {
        JsonElement jsonElement = this.jsonObject.get(key);
        if (jsonElement instanceof JsonNull) {
            return null;
        }

        return jsonElement;
    }

    @Override
    public JsonConfiguration getOrDefaultIf(String key, JsonConfiguration def, Predicate<JsonConfiguration> predicate) {
        JsonElement jsonElement = this.getElement(key);
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return def;
        }

        JsonConfiguration result = new JsonConfiguration(jsonElement.getAsJsonObject());
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public <T> T getOrDefaultIf(String key, Type type, T def, Predicate<T> predicate) {
        JsonElement jsonElement = this.getElement(key);
        if (jsonElement == null) {
            return def;
        }

        T result = this.gson.fromJson(jsonElement, type);
        if (predicate.test(result)) {
            return result;
        }

        return def;
    }

    @Override
    public <T> T getOrDefaultIf(String key, Class<T> type, T def, Predicate<T> predicate) {
        JsonElement jsonElement = this.getElement(key);
        if (jsonElement == null) {
            return def;
        }

        T result = this.gson.fromJson(jsonElement, type);
        if (predicate.test(result)) {
            return result;
        }

        return def;
    }

    @Override
    public String getOrDefaultIf(String key, String def, Predicate<String> predicate) {
        if (!this.has(key)) {
            return def;
        }

        String result = this.jsonObject.get(key).getAsString();
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public Integer getOrDefaultIf(String key, Integer def, Predicate<Integer> predicate) {
        if (!this.has(key)) {
            return def;
        }

        Integer result = this.jsonObject.get(key).getAsInt();
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public Long getOrDefaultIf(String key, Long def, Predicate<Long> predicate) {
        if (!this.has(key)) {
            return def;
        }

        Long result = this.jsonObject.get(key).getAsLong();
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public Short getOrDefaultIf(String key, Short def, Predicate<Short> predicate) {
        if (!this.has(key)) {
            return def;
        }

        Short result = this.jsonObject.get(key).getAsShort();
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public Byte getOrDefaultIf(String key, Byte def, Predicate<Byte> predicate) {
        if (!this.has(key)) {
            return def;
        }

        Byte result = this.jsonObject.get(key).getAsByte();
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public Boolean getOrDefaultIf(String key, Boolean def, Predicate<Boolean> predicate) {
        if (!this.has(key)) {
            return def;
        }

        Boolean result = this.jsonObject.get(key).getAsBoolean();
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public Double getOrDefaultIf(String key, Double def, Predicate<Double> predicate) {
        if (!this.has(key)) {
            return def;
        }

        Double result = this.jsonObject.get(key).getAsDouble();
        if (predicate.test(result)) {
            return result;
        }

        return def;
    }

    @Override
    public Float getOrDefaultIf(String key, Float def, Predicate<Float> predicate) {
        if (!this.has(key)) {
            return def;
        }

        Float result = this.jsonObject.get(key).getAsFloat();
        if (predicate.test(result)) {
            return result;
        }

        return def;
    }

    @Override
    public boolean has(String key) {
        return this.jsonObject.has(key) && this.getElement(key) != null;
    }

    @Override
    public void write(Path path) {
        IOUtils.deleteFile(path.toFile());
        IOUtils.createFile(path);

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            this.gson.toJson(this.jsonObject, outputStreamWriter);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void write(String path) {
        this.write(Paths.get(path));
    }

    @Override
    public void write(File path) {
        this.write(path.toPath());
    }

    @NotNull
    @Override
    public String toPrettyString() {
        return this.gson.toJson(this.jsonObject);
    }

    @NotNull
    @Override
    public byte[] toPrettyBytes() {
        return this.toPrettyString().getBytes(StandardCharsets.UTF_8);
    }

    @NotNull
    @Override
    public Map<String, JsonElement> asMap() {
        Map<String, JsonElement> out = new HashMap<>();
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : this.jsonObject.entrySet()) {
            out.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue());
        }

        return out;
    }

    @NotNull
    @Override
    public JsonConfiguration copy() {
        return new JsonConfiguration(this.jsonObject.deepCopy());
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }

    @NotNull
    public Gson getGson() {
        return this.gson;
    }
}
