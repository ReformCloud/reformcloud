package de.klaro.reformcloud2.executor.api.common.utility.maps;

import java.util.HashMap;

public final class AbsentMap<K, V> extends HashMap<K, V> {

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
