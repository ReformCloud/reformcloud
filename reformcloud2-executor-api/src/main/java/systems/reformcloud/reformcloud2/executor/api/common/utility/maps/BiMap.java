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
