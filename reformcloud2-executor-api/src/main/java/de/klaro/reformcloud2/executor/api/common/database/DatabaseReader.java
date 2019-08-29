package de.klaro.reformcloud2.executor.api.common.database;

import de.klaro.reformcloud2.executor.api.common.utility.annotiations.Nullable;

public interface DatabaseReader<T, K, V> extends Iterable<T> {

    DatabaseResult<T> find(K key);

    DatabaseResult<T> findIfAbsent(@Nullable K key, V identifier);

    DatabaseResult<T> insert(K key, V identifier);

    DatabaseResult<Void> remove(K key);

    DatabaseResult<Void> removeIfAbsent(V identifier);

    DatabaseResult<Boolean> contains(K key);

    DatabaseResult<Integer> size();
}
