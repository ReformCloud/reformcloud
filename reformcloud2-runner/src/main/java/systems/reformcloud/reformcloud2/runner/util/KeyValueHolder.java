package systems.reformcloud.reformcloud2.runner.util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class KeyValueHolder<K, V> implements Map.Entry<K, V> {

    public KeyValueHolder(@NotNull K key, @NotNull V value) {
        this.key = key;
        this.value = value;
    }

    private final K key;

    private V value;

    @Override
    @NotNull
    public K getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public V getValue() {
        return this.value;
    }

    @Override
    @NotNull
    public V setValue(@NotNull V value) {
        return this.value = value;
    }
}
