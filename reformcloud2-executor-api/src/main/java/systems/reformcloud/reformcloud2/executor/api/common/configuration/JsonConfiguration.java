package systems.reformcloud.reformcloud2.executor.api.common.configuration;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.gson.InternalJsonParser;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

public final class JsonConfiguration
    implements Configurable<JsonConfiguration> {

  public static final ThreadLocal<Gson> GSON =
      ThreadLocal.withInitial(()
                                  -> new GsonBuilder()
                                         .setPrettyPrinting()
                                         .serializeNulls()
                                         .disableHtmlEscaping()
                                         .create());

  public JsonConfiguration() {}

  public JsonConfiguration(String json) {
    JsonElement jsonElement;
    try {
      jsonElement = InternalJsonParser.parseString(json);
    } catch (final Exception ex) {
      jsonElement = new JsonObject();
    }

    Conditions.isTrue(jsonElement.isJsonObject(),
                      "JsonElement has to be a json object");
    this.jsonObject = jsonElement.getAsJsonObject();
  }

  public JsonConfiguration(InputStream stream) {
    try (InputStreamReader inputStreamReader =
             new InputStreamReader(stream, StandardCharsets.UTF_8)) {
      JsonElement jsonElement;
      try {
        jsonElement = InternalJsonParser.parseReader(inputStreamReader);
      } catch (final Exception ex) {
        jsonElement = new JsonObject();
      }

      Conditions.isTrue(jsonElement.isJsonObject(),
                        "JsonElement has to be a json object");
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

    Conditions.isTrue(jsonElement.isJsonObject(),
                      "JsonElement has to be a json object");
    this.jsonObject = jsonElement.getAsJsonObject();
  }

  public JsonConfiguration(JsonObject jsonObject) {
    this.jsonObject = jsonObject;
  }

  private JsonObject jsonObject = new JsonObject();

  @Nonnull
  @Override
  public JsonConfiguration add(@Nonnull String key,
                               @Nullable JsonConfiguration value) {
    if (value == null) {
      jsonObject.add(key, JsonNull.INSTANCE);
      return this;
    }

    this.jsonObject.add(key, value.jsonObject);
    return this;
  }

  @Nonnull
  @Override
  public JsonConfiguration add(@Nonnull String key, @Nullable Object value) {
    if (value == null) {
      jsonObject.add(key, JsonNull.INSTANCE);
      return this;
    }

    this.jsonObject.add(key, GSON.get().toJsonTree(value));
    return this;
  }

  @Nonnull
  @Override
  public JsonConfiguration add(@Nonnull String key, @Nullable String value) {
    this.jsonObject.addProperty(key, value);
    return this;
  }

  @Nonnull
  @Override
  public JsonConfiguration add(@Nonnull String key, @Nullable Integer value) {
    this.jsonObject.addProperty(key, value);
    return this;
  }

  @Nonnull
  @Override
  public JsonConfiguration add(@Nonnull String key, @Nullable Long value) {
    this.jsonObject.addProperty(key, value);
    return this;
  }

  @Nonnull
  @Override
  public JsonConfiguration add(@Nonnull String key, @Nullable Short value) {
    this.jsonObject.addProperty(key, value);
    return this;
  }

  @Nonnull
  @Override
  public JsonConfiguration add(@Nonnull String key, @Nullable Byte value) {
    this.jsonObject.addProperty(key, value);
    return this;
  }

  @Nonnull
  @Override
  public JsonConfiguration add(@Nonnull String key, @Nullable Boolean value) {
    this.jsonObject.addProperty(key, value);
    return this;
  }

  @Nonnull
  @Override
  public JsonConfiguration add(@Nonnull String key, @Nullable Double value) {
    this.jsonObject.addProperty(key, value);
    return this;
  }

  @Nonnull
  @Override
  public JsonConfiguration add(@Nonnull String key, @Nullable Float value) {
    this.jsonObject.addProperty(key, value);
    return this;
  }

  @Nonnull
  @Override
  public JsonConfiguration remove(@Nonnull String key) {
    this.jsonObject.remove(key);
    return this;
  }

  @Nonnull
  @Override
  public JsonConfiguration get(@Nonnull String key) {
    return getOrDefault(key, new JsonConfiguration());
  }

  @Override
  public <T> T get(@Nonnull String key, @Nonnull TypeToken<T> type) {
    return getOrDefault(key, type.getType(), null);
  }

  @Override
  public <T> T get(@Nonnull String key, @Nonnull Class<T> type) {
    return getOrDefault(key, type, null);
  }

  @Nonnull
  @Override
  public String getString(@Nonnull String key) {
    return getOrDefault(key, "");
  }

  @Nonnull
  @Override
  public Integer getInteger(String key) {
    return getOrDefault(key, -1);
  }

  @Nonnull
  @Override
  public Long getLong(String key) {
    return getOrDefault(key, (long)-1);
  }

  @Nonnull
  @Override
  public Short getShort(String key) {
    return getOrDefault(key, (short)-1);
  }

  @Nonnull
  @Override
  public Byte getByte(String key) {
    return getOrDefault(key, (byte)-1);
  }

  @Nonnull
  @Override
  public Boolean getBoolean(String key) {
    return getOrDefault(key, false);
  }

  @Nonnull
  @Override
  public Double getDouble(String key) {
    return getOrDefault(key, -1D);
  }

  @Nonnull
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
  public JsonConfiguration
  getOrDefaultIf(String key, JsonConfiguration def,
                 Predicate<JsonConfiguration> predicate) {
    JsonElement jsonElement = this.getElement(key);
    if (jsonElement == null || !jsonElement.isJsonObject()) {
      return def;
    }

    JsonConfiguration result =
        new JsonConfiguration(jsonElement.getAsJsonObject());
    if (predicate.test(result)) {
      return result;
    }
    return def;
  }

  @Override
  public <T> T getOrDefaultIf(String key, Type type, T def,
                              Predicate<T> predicate) {
    JsonElement jsonElement = this.getElement(key);
    if (jsonElement == null) {
      return def;
    }

    T result = GSON.get().fromJson(jsonElement, type);
    if (predicate.test(result)) {
      return result;
    }

    return def;
  }

  @Override
  public <T> T getOrDefaultIf(String key, Class<T> type, T def,
                              Predicate<T> predicate) {
    JsonElement jsonElement = this.getElement(key);
    if (jsonElement == null) {
      return def;
    }

    T result = GSON.get().fromJson(jsonElement, type);
    if (predicate.test(result)) {
      return result;
    }

    return def;
  }

  @Override
  public String getOrDefaultIf(String key, String def,
                               Predicate<String> predicate) {
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
  public Integer getOrDefaultIf(String key, Integer def,
                                Predicate<Integer> predicate) {
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
  public Short getOrDefaultIf(String key, Short def,
                              Predicate<Short> predicate) {
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
  public Boolean getOrDefaultIf(String key, Boolean def,
                                Predicate<Boolean> predicate) {
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
  public Double getOrDefaultIf(String key, Double def,
                               Predicate<Double> predicate) {
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
  public Float getOrDefaultIf(String key, Float def,
                              Predicate<Float> predicate) {
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

    try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
             Files.newOutputStream(path), StandardCharsets.UTF_8)) {
      GSON.get().toJson(jsonObject, outputStreamWriter);
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

  @Nonnull
  @Override
  public String toPrettyString() {
    return GSON.get().toJson(jsonObject);
  }

  @Override
  public byte[] toPrettyBytes() {
    return toPrettyString().getBytes(StandardCharsets.UTF_8);
  }

  public static JsonConfiguration read(Path path) {
    if (!Files.exists(path)) {
      return new JsonConfiguration();
    }

    try (InputStreamReader inputStreamReader = new InputStreamReader(
             Files.newInputStream(path), StandardCharsets.UTF_8)) {
      return new JsonConfiguration(inputStreamReader);
    } catch (final IOException ex) {
      ex.printStackTrace();
    }

    return new JsonConfiguration();
  }

  public static JsonConfiguration read(String path) {
    return read(Paths.get(path));
  }

  public static JsonConfiguration read(File path) {
    return read(path.toPath());
  }
}
