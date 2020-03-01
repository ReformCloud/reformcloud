package systems.reformcloud.reformcloud2.executor.api.common.utility.maps;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a hash map which return either the value which the map already contains or inserts the
 * missing value and returns the defined value by the user
 *
 * @param <K> The type of the key objects in the map
 * @param <V> The type of the value objects in the map
 */
public final class AbsentMap<K, V> extends ConcurrentHashMap<K, V> {

    @Override
    public V putIfAbsent(K key, V value) {
        V out = get(key);
        if (out == null) {
            put(key, value);
            return value;
        }

        return out;
    }
}
