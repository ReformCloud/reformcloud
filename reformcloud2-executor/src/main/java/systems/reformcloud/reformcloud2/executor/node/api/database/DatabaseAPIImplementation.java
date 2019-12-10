package systems.reformcloud.reformcloud2.executor.node.api.database;

import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * @deprecated This class is currently deprecated because it can lead to thread deadlocks
 */
@Deprecated
public class DatabaseAPIImplementation implements DatabaseAsyncAPI, DatabaseSyncAPI {

    public DatabaseAPIImplementation(Database parent) {
        this.database = parent;
    }

    private final Database database;

    @Nonnull
    @Override
    public Task<JsonConfiguration> findAsync(@Nonnull String table, @Nonnull String key, String identifier) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            JsonConfiguration jsonConfiguration = this.database.createForTable(table).find(key).getUninterruptedly();
            if (jsonConfiguration == null) {
                jsonConfiguration = this.database.createForTable(table).findIfAbsent(identifier).getUninterruptedly();
            }

            task.complete(jsonConfiguration);
        });
        return task;
    }

    @Nonnull
    @Override
    public <T> Task<T> findAsync(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull Function<JsonConfiguration, T> function) {
        Task<T> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(function.apply(find(table, key, identifier))));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> insertAsync(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull JsonConfiguration data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.database.createForTable(table).insert(key, identifier, data);
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> updateAsync(@Nonnull String table, @Nonnull String key, @Nonnull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(this.database.createForTable(table).update(key, newData).getUninterruptedly()));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> updateIfAbsentAsync(@Nonnull String table, @Nonnull String identifier, @Nonnull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(this.database.createForTable(table).updateIfAbsent(identifier, newData).getUninterruptedly()));
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> removeAsync(@Nonnull String table, @Nonnull String key) {
        return database.createForTable(table).remove(key);
    }

    @Nonnull
    @Override
    public Task<Void> removeIfAbsentAsync(@Nonnull String table, @Nonnull String identifier) {
        return database.createForTable(table).removeIfAbsent(identifier);
    }

    @Nonnull
    @Override
    public Task<Boolean> createDatabaseAsync(@Nonnull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(database.createDatabase(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> deleteDatabaseAsync(@Nonnull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(database.deleteDatabase(name)));
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> containsAsync(@Nonnull String table, @Nonnull String key) {
        return database.createForTable(table).contains(key);
    }

    @Nonnull
    @Override
    public Task<Integer> sizeAsync(@Nonnull String table) {
        return database.createForTable(table).size();
    }

    @Override
    public JsonConfiguration find(@Nonnull String table, @Nonnull String key, String identifier) {
        return findAsync(table, key, identifier).getUninterruptedly();
    }

    @Override
    public <T> T find(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull Function<JsonConfiguration, T> function) {
        return findAsync(table, key, identifier, function).getUninterruptedly();
    }

    @Override
    public void insert(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull JsonConfiguration data) {
        insertAsync(table, key, identifier, data).awaitUninterruptedly();
    }

    @Override
    public boolean update(@Nonnull String table, @Nonnull String key, @Nonnull JsonConfiguration newData) {
        return updateAsync(table, key, newData).getUninterruptedly();
    }

    @Override
    public boolean updateIfAbsent(@Nonnull String table, @Nonnull String identifier, @Nonnull JsonConfiguration newData) {
        return updateIfAbsentAsync(table, identifier, newData).getUninterruptedly();
    }

    @Override
    public void remove(@Nonnull String table, @Nonnull String key) {
        removeAsync(table, key).awaitUninterruptedly();
    }

    @Override
    public void removeIfAbsent(@Nonnull String table, @Nonnull String identifier) {
        removeIfAbsentAsync(table, identifier).awaitUninterruptedly();
    }

    @Override
    public boolean createDatabase(@Nonnull String name) {
        return createDatabaseAsync(name).getUninterruptedly();
    }

    @Override
    public boolean deleteDatabase(@Nonnull String name) {
        return deleteDatabaseAsync(name).getUninterruptedly();
    }

    @Override
    public boolean contains(@Nonnull String table, @Nonnull String key) {
        return containsAsync(table, key).getUninterruptedly();
    }

    @Override
    public int size(@Nonnull String table) {
        return sizeAsync(table).getUninterruptedly();
    }
}
