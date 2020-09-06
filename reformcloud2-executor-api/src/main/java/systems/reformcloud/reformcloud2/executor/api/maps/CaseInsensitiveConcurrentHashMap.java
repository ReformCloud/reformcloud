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
package systems.reformcloud.reformcloud2.executor.api.maps;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CaseInsensitiveConcurrentHashMap<V> extends ConcurrentHashMap<String, V> {

    public CaseInsensitiveConcurrentHashMap() {
    }

    public CaseInsensitiveConcurrentHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public CaseInsensitiveConcurrentHashMap(Map<? extends String, ? extends V> m) {
        super(m);
    }

    public CaseInsensitiveConcurrentHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CaseInsensitiveConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(((String) key).toLowerCase());
    }

    @Override
    public V put(@NotNull String key, @NotNull V value) {
        return super.put(key.toLowerCase(), value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        for (Entry<? extends String, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V putIfAbsent(@NotNull String key, V value) {
        return super.putIfAbsent(key.toLowerCase(), value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return super.remove(((String) key).toLowerCase(), value);
    }

    @Override
    public boolean replace(@NotNull String key, @NotNull V oldValue, @NotNull V newValue) {
        return super.replace(key.toLowerCase(), oldValue, newValue);
    }

    @Override
    public V replace(@NotNull String key, @NotNull V value) {
        return super.replace(key.toLowerCase(), value);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return super.getOrDefault(((String) key).toLowerCase(), defaultValue);
    }

    @Override
    public V computeIfAbsent(String key, @NotNull Function<? super String, ? extends V> mappingFunction) {
        return super.computeIfAbsent(key.toLowerCase(), mappingFunction);
    }

    @Override
    public V computeIfPresent(String key, @NotNull BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
        return super.computeIfPresent(key.toLowerCase(), remappingFunction);
    }

    @Override
    public V compute(String key, @NotNull BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
        return super.compute(key.toLowerCase(), remappingFunction);
    }

    @Override
    public V merge(String key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return super.merge(key.toLowerCase(), value, remappingFunction);
    }
}
