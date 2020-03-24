package systems.reformcloud.reformcloud2.runner.util;

import javax.annotation.Nonnull;
import java.util.Map;

public final class KeyValueHolder<K, V> implements Map.Entry<K, V> {

    public KeyValueHolder(@Nonnull K key, @Nonnull V value) {
        this.key = key;
        this.value = value;
    }

    private final K key;

    private V value;

    @Override
    @Nonnull
    public K getKey() {
        return this.key;
    }

    @Override
    @Nonnull
    public V getValue() {
        return this.value;
    }

    @Override
    @Nonnull
    public V setValue(@Nonnull V value) {
        return this.value = value;
    }
}
