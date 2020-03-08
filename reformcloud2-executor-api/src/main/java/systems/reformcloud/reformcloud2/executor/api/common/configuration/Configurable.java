package systems.reformcloud.reformcloud2.executor.api.common.configuration;

import com.google.gson.reflect.TypeToken;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * Represents any configuration used in the cloud system
 */
public interface Configurable<V extends Configurable<V>> {

    @Nonnull
    V add(@Nonnull String key, @Nullable V value);

    @Nonnull
    V add(@Nonnull String key, @Nullable Object value);

    @Nonnull
    V add(@Nonnull String key, @Nullable String value);

    @Nonnull
    V add(@Nonnull String key, @Nullable Integer value);

    @Nonnull
    V add(@Nonnull String key, @Nullable Long value);

    @Nonnull
    V add(@Nonnull String key, @Nullable Short value);

    @Nonnull
    V add(@Nonnull String key, @Nullable Byte value);

    @Nonnull
    V add(@Nonnull String key, @Nullable Boolean value);

    @Nonnull
    V add(@Nonnull String key, @Nullable Double value);

    @Nonnull
    V add(@Nonnull String key, @Nullable Float value);

    @Nonnull
    V remove(@Nonnull String key);

    @Nonnull
    V get(@Nonnull String key);

    @Nullable
    <T> T get(@Nonnull String key, @Nonnull TypeToken<T> type);

    @Nullable
    <T> T get(@Nonnull String key, @Nonnull Class<T> type);

    @Nonnull
    @CheckReturnValue
    String getString(@Nonnull String key);

    @Nonnull
    @CheckReturnValue
    Integer getInteger(String key);

    @Nonnull
    @CheckReturnValue
    Long getLong(String key);

    @Nonnull
    @CheckReturnValue
    Short getShort(String key);

    @Nonnull
    @CheckReturnValue
    Byte getByte(String key);

    @Nonnull
    @CheckReturnValue
    Boolean getBoolean(String key);

    @Nonnull
    @CheckReturnValue
    Double getDouble(String key);

    @Nonnull
    @CheckReturnValue
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

    @Nonnull
    String toPrettyString();

    byte[] toPrettyBytes();

    @Nonnull
    V copy();
}
