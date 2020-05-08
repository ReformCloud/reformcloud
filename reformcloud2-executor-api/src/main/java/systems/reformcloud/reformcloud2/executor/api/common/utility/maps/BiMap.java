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
package systems.reformcloud.reformcloud2.executor.api.common.utility.maps;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BiMap<K, V> {

    private final AbsentMap<K, Collection<V>> parent = new AbsentMap<>();

    public void add(@NotNull K key, @NotNull V value) {
        this.parent.putIfAbsent(key, new CopyOnWriteArrayList<>()).add(value);
    }

    public int size(@NotNull K key) {
        Collection<V> collection = this.parent.get(key);
        return collection == null ? 0 : collection.size();
    }

    public void removeRandom(@NotNull K key) {
        Collection<V> all = parent.get(key);
        if (all == null) {
            return;
        }

        all.stream().findAny().ifPresent(all::remove);
    }

    public void removeAllOf(@NotNull K key, @NotNull V value) {
        Collection<V> get = this.parent.get(key);
        if (get == null) {
            return;
        }

        get.stream().filter(e -> e.equals(value)).forEach(get::remove);
    }

    public void removeAll(@NotNull K key) {
        this.parent.remove(key);
    }

}
