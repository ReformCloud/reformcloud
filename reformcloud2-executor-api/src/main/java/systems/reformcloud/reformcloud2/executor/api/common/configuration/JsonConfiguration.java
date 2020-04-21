package systems.reformcloud.reformcloud2.executor.api.common.configuration;

import com.google.gson.*;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.gson.InternalJsonParser;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.gson.JsonConfigurationTypeAdapter;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

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

    public JsonConfiguration() {
    }

    public JsonConfiguration(@NotNull Gson gson) {
        this.gson = gson;
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

    private JsonObject jsonObject = new JsonObject();

    @NotNull
    @Override
    public JsonConfiguration add(@NotNull String key, @Nullable JsonConfiguration value) {
        if (value == null) {
            jsonObject.add(key, JsonNull.INSTANCE);
            return this;
        }

        this.jsonObject.add(key, value.jsonObject);
        return this;
    }

    @NotNull
    @Override
    public JsonConfiguration add(@NotNull String key, @Nullable Object value) {
        if (value == null) {
            jsonObject.add(key, JsonNull.INSTANCE);
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
        return getOrDefault(key, new JsonConfiguration());
    }

    @Override
    public <T> T get(@NotNull String key, @NotNull TypeToken<T> type) {
        return getOrDefault(key, type.getType(), null);
    }

    @Override
    public <T> T get(@NotNull String key, @NotNull Class<T> type) {
        return getOrDefault(key, type, null);
    }

    @NotNull
    @Override
    public String getString(@NotNull String key) {
        return getOrDefault(key, "");
    }

    @NotNull
    @Override
    public Integer getInteger(String key) {
        return getOrDefault(key, -1);
    }

    @NotNull
    @Override
    public Long getLong(String key) {
        return getOrDefault(key, (long) -1);
    }

    @NotNull
    @Override
    public Short getShort(String key) {
        return getOrDefault(key, (short) -1);
    }

    @NotNull
    @Override
    public Byte getByte(String key) {
        return getOrDefault(key, (byte) -1);
    }

    @NotNull
    @Override
    public Boolean getBoolean(String key) {
        return getOrDefault(key, false);
    }

    @NotNull
    @Override
    public Double getDouble(String key) {
        return getOrDefault(key, -1D);
    }

    @NotNull
    @Override
    public Float getFloat(String key) {
        return getOrDefault(key, -1F);
    }

    @Override
    public JsonConfiguration getOrDefault(String key, JsonConfiguration def) {
        return getOrDefaultIf(key, def, jsonConfiguration -> true);
    }

    @Override
    public <T> T getOrDefault(String key, Type type, T def) {
        return getOrDefaultIf(key, type, def, t -> true);
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> type, T def) {
        return getOrDefaultIf(key, type, def, t -> true);
    }

    @Override
    public String getOrDefault(String key, String def) {
        return getOrDefaultIf(key, def, s -> true);
    }

    @Override
    public Integer getOrDefault(String key, Integer def) {
        return getOrDefaultIf(key, def, integer -> true);
    }

    @Override
    public Long getOrDefault(String key, Long def) {
        return getOrDefaultIf(key, def, aLong -> true);
    }

    @Override
    public Short getOrDefault(String key, Short def) {
        return getOrDefaultIf(key, def, aShort -> true);
    }

    @Override
    public Byte getOrDefault(String key, Byte def) {
        return getOrDefaultIf(key, def, aByte -> true);
    }

    @Override
    public Boolean getOrDefault(String key, Boolean def) {
        return getOrDefaultIf(key, def, aBoolean -> true);
    }

    @Override
    public Double getOrDefault(String key, Double def) {
        return getOrDefaultIf(key, def, s -> true);
    }

    @Override
    public Float getOrDefault(String key, Float def) {
        return getOrDefaultIf(key, def, aFloat -> true);
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
        if (!has(key)) {
            return def;
        }

        String result = jsonObject.get(key).getAsString();
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public Integer getOrDefaultIf(String key, Integer def, Predicate<Integer> predicate) {
        if (!has(key)) {
            return def;
        }

        Integer result = jsonObject.get(key).getAsInt();
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public Long getOrDefaultIf(String key, Long def, Predicate<Long> predicate) {
        if (!has(key)) {
            return def;
        }

        Long result = jsonObject.get(key).getAsLong();
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public Short getOrDefaultIf(String key, Short def, Predicate<Short> predicate) {
        if (!has(key)) {
            return def;
        }

        Short result = jsonObject.get(key).getAsShort();
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public Byte getOrDefaultIf(String key, Byte def, Predicate<Byte> predicate) {
        if (!has(key)) {
            return def;
        }

        Byte result = jsonObject.get(key).getAsByte();
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public Boolean getOrDefaultIf(String key, Boolean def, Predicate<Boolean> predicate) {
        if (!has(key)) {
            return def;
        }

        Boolean result = jsonObject.get(key).getAsBoolean();
        if (predicate.test(result)) {
            return result;
        }
        return def;
    }

    @Override
    public Double getOrDefaultIf(String key, Double def, Predicate<Double> predicate) {
        if (!has(key)) {
            return def;
        }

        Double result = jsonObject.get(key).getAsDouble();
        if (predicate.test(result)) {
            return result;
        }

        return def;
    }

    @Override
    public Float getOrDefaultIf(String key, Float def, Predicate<Float> predicate) {
        if (!has(key)) {
            return def;
        }

        Float result = jsonObject.get(key).getAsFloat();
        if (predicate.test(result)) {
            return result;
        }

        return def;
    }

    @Override
    public boolean has(String key) {
        return jsonObject.has(key) && getElement(key) != null;
    }

    @Override
    public void write(Path path) {
        SystemHelper.deleteFile(path.toFile());
        SystemHelper.createFile(path);

        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            this.gson.toJson(jsonObject, outputStreamWriter);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void write(String path) {
        write(Paths.get(path));
    }

    @Override
    public void write(File path) {
        write(path.toPath());
    }

    @NotNull
    @Override
    public String toPrettyString() {
        return this.gson.toJson(jsonObject);
    }

    @NotNull
    @Override
    public byte[] toPrettyBytes() {
        return toPrettyString().getBytes(StandardCharsets.UTF_8);
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
        return jsonObject;
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

    @NotNull
    public Gson getGson() {
        return gson;
    }

    public static JsonConfiguration read(String path) {
        return read(Paths.get(path));
    }

    public static JsonConfiguration read(File path) {
        return read(path.toPath());
    }
}
