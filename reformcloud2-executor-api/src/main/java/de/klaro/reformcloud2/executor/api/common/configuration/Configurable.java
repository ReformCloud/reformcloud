package de.klaro.reformcloud2.executor.api.common.configuration;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.function.Predicate;

public interface Configurable<V extends Configurable> {

    Configurable add(String key, V value);

    Configurable add(String key, Object value);

    Configurable add(String key, String value);

    Configurable add(String key, Integer value);

    Configurable add(String key, Long value);

    Configurable add(String key, Short value);

    Configurable add(String key, Byte value);

    Configurable add(String key, Boolean value);

    Configurable remove(String key);

    V get(String key);

    <T> T get(String key, Type type);

    <T> T get(String key, Class<T> type);

    String getString(String key);

    Integer getInteger(String key);

    Long getLong(String key);

    Short getShort(String key);

    Byte getByte(String key);

    Boolean getBoolean(String key);

    V getOrDefault(String key, V def);

    <T> T getOrDefault(String key, Type type, T def);

    <T> T getOrDefault(String key, Class<T> type, T def);

    String getOrDefault(String key, String def);

    Integer getOrDefault(String key, Integer def);

    Long getOrDefault(String key, Long def);

    Short getOrDefault(String key, Short def);

    Byte getOrDefault(String key, Byte def);

    Boolean getOrDefault(String key, Boolean def);

    V getOrDefaultIf(String key, V def, Predicate<V> predicate);

    <T> T getOrDefaultIf(String key, Type type, T def, Predicate<T> predicate);

    <T> T getOrDefaultIf(String key, Class<T> type, T def, Predicate<T> predicate);

    String getOrDefaultIf(String key, String def, Predicate<String> predicate);

    Integer getOrDefaultIf(String key, Integer def, Predicate<Integer> predicate);

    Long getOrDefaultIf(String key, Long def, Predicate<Long> predicate);

    Short getOrDefaultIf(String key, Short def, Predicate<Short> predicate);

    Byte getOrDefaultIf(String key, Byte def, Predicate<Byte> predicate);

    Boolean getOrDefaultIf(String key, Boolean def, Predicate<Boolean> predicate);

    boolean has(String key);

    void write(Path path);

    void write(String path);

    void write(File path);

    String toPrettyString();

    byte[] toPrettyBytes();
}
