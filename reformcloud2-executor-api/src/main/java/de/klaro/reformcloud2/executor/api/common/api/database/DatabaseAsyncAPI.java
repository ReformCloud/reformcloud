package de.klaro.reformcloud2.executor.api.common.api.database;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.utility.annotiations.Nullable;
import de.klaro.reformcloud2.executor.api.common.utility.task.Task;

import java.util.function.Function;

public interface DatabaseAsyncAPI extends DatabaseSyncAPI {

    Task<JsonConfiguration> findAsync(String table, String key, @Nullable String identifier);

    <T> Task<T> findAsync(String table, String key, @Nullable String identifier, Function<JsonConfiguration, T> function);

    Task<Void> insertAsync(String table, String key, @Nullable String identifier, JsonConfiguration data);

    Task<Boolean> updateAsync(String table, String key, JsonConfiguration newData);

    Task<Boolean> updateIfAbsentAsync(String table, String identifier, JsonConfiguration newData);

    Task<Void> removeAsync(String table, String key);

    Task<Void> removeIfAbsentAsync(String table, String identifier);

    Task<Boolean> createDatabaseAsync(String name);

    Task<Boolean> deleteDatabaseAsync(String name);

    Task<Boolean> containsAsync(String table, String key);

    Task<Integer> sizeAsync(String table);
}
