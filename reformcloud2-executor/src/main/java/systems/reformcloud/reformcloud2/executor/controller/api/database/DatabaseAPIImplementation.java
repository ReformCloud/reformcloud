package systems.reformcloud.reformcloud2.executor.controller.api.database;

import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class DatabaseAPIImplementation implements DatabaseAsyncAPI, DatabaseSyncAPI {

    public DatabaseAPIImplementation(Database<?> parent) {
        this.database = parent;
    }

    private final Database<?> database;

    @Nonnull
    @Override
    public Task<JsonConfiguration> findAsync(@Nonnull String table, @Nonnull String key, String identifier) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            DatabaseReader databaseReader = this.database.createForTable(table);
            if (databaseReader == null) {
                task.complete(null);
                return;
            }

            JsonConfiguration result = databaseReader.find(key).getUninterruptedly();
            if (result != null) {
                task.complete(result);
            } else if (identifier != null) {
                task.complete(databaseReader.findIfAbsent(identifier).getUninterruptedly());
            } else {
                task.complete(null);
            }
        });
        return task;
    }

    @Nonnull
    @Override
    public <T> Task<T> findAsync(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull Function<JsonConfiguration, T> function) {
        Task<T> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            JsonConfiguration jsonConfiguration = findAsync(table, key, identifier).getUninterruptedly();
            if (jsonConfiguration != null) {
                task.complete(function.apply(jsonConfiguration));
            } else {
                task.complete(null);
            }
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Void> insertAsync(@Nonnull String table, @Nonnull String key, String identifier, @Nonnull JsonConfiguration data) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            DatabaseReader databaseReader = database.createForTable(table);
            if (databaseReader == null) {
                task.complete(null);
                return;
            }

            databaseReader.insert(key, identifier, data).awaitUninterruptedly();
            task.complete(null);
        });
        return task;
    }

    @Nonnull
    @Override
    public Task<Boolean> updateAsync(@Nonnull String table, @Nonnull String key, @Nonnull JsonConfiguration newData) {
        DatabaseReader databaseReader = database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(false);
        }

        return databaseReader.update(key, newData);
    }

    @Nonnull
    @Override
    public Task<Boolean> updateIfAbsentAsync(@Nonnull String table, @Nonnull String identifier, @Nonnull JsonConfiguration newData) {
        DatabaseReader databaseReader = database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.updateIfAbsent(identifier, newData);
    }

    @Nonnull
    @Override
    public Task<Void> removeAsync(@Nonnull String table, @Nonnull String key) {
        DatabaseReader databaseReader = database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.remove(key);
    }

    @Nonnull
    @Override
    public Task<Void> removeIfAbsentAsync(@Nonnull String table, @Nonnull String identifier) {
        DatabaseReader databaseReader = database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.removeIfAbsent(identifier);
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
        DatabaseReader databaseReader = database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.contains(key);
    }

    @Nonnull
    @Override
    public Task<Integer> sizeAsync(@Nonnull String table) {
        DatabaseReader databaseReader = database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.size();
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
        Boolean result = updateAsync(table, key, newData).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean updateIfAbsent(@Nonnull String table, @Nonnull String identifier, @Nonnull JsonConfiguration newData) {
        Boolean result = updateIfAbsentAsync(table, identifier, newData).getUninterruptedly();
        return result == null ? false : result;
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
        Boolean result = createDatabaseAsync(name).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean deleteDatabase(@Nonnull String name) {
        Boolean result = deleteDatabaseAsync(name).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean contains(@Nonnull String table, @Nonnull String key) {
        Boolean result = containsAsync(table, key).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public int size(@Nonnull String table) {
        Integer result = sizeAsync(table).getUninterruptedly();
        return result == null ? 0 : result;
    }
}
