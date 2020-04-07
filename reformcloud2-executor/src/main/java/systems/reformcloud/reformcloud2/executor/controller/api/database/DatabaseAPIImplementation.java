package systems.reformcloud.reformcloud2.executor.controller.api.database;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.function.Function;

public class DatabaseAPIImplementation implements DatabaseAsyncAPI, DatabaseSyncAPI {

    public DatabaseAPIImplementation(Database<?> parent) {
        this.database = parent;
    }

    private final Database<?> database;

    @NotNull
    @Override
    public Task<JsonConfiguration> findAsync(@NotNull String table, @NotNull String key, String identifier) {
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

    @NotNull
    @Override
    public <T> Task<T> findAsync(@NotNull String table, @NotNull String key, String identifier, @NotNull Function<JsonConfiguration, T> function) {
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

    @NotNull
    @Override
    public Task<Void> insertAsync(@NotNull String table, @NotNull String key, String identifier, @NotNull JsonConfiguration data) {
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

    @NotNull
    @Override
    public Task<Boolean> updateAsync(@NotNull String table, @NotNull String key, @NotNull JsonConfiguration newData) {
        DatabaseReader databaseReader = database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(false);
        }

        return databaseReader.update(key, newData);
    }

    @NotNull
    @Override
    public Task<Boolean> updateIfAbsentAsync(@NotNull String table, @NotNull String identifier, @NotNull JsonConfiguration newData) {
        DatabaseReader databaseReader = database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.updateIfAbsent(identifier, newData);
    }

    @NotNull
    @Override
    public Task<Void> removeAsync(@NotNull String table, @NotNull String key) {
        DatabaseReader databaseReader = database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.remove(key);
    }

    @NotNull
    @Override
    public Task<Void> removeIfAbsentAsync(@NotNull String table, @NotNull String identifier) {
        DatabaseReader databaseReader = database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.removeIfAbsent(identifier);
    }

    @NotNull
    @Override
    public Task<Boolean> createDatabaseAsync(@NotNull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(database.createDatabase(name)));
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> deleteDatabaseAsync(@NotNull String name) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(database.deleteDatabase(name)));
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> containsAsync(@NotNull String table, @NotNull String key) {
        DatabaseReader databaseReader = database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.contains(key);
    }

    @NotNull
    @Override
    public Task<Integer> sizeAsync(@NotNull String table) {
        DatabaseReader databaseReader = database.createForTable(table);
        if (databaseReader == null) {
            return Task.completedTask(null);
        }

        return databaseReader.size();
    }

    @Override
    public JsonConfiguration find(@NotNull String table, @NotNull String key, String identifier) {
        return findAsync(table, key, identifier).getUninterruptedly();
    }

    @Override
    public <T> T find(@NotNull String table, @NotNull String key, String identifier, @NotNull Function<JsonConfiguration, T> function) {
        return findAsync(table, key, identifier, function).getUninterruptedly();
    }

    @Override
    public void insert(@NotNull String table, @NotNull String key, String identifier, @NotNull JsonConfiguration data) {
        insertAsync(table, key, identifier, data).awaitUninterruptedly();
    }

    @Override
    public boolean update(@NotNull String table, @NotNull String key, @NotNull JsonConfiguration newData) {
        Boolean result = updateAsync(table, key, newData).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean updateIfAbsent(@NotNull String table, @NotNull String identifier, @NotNull JsonConfiguration newData) {
        Boolean result = updateIfAbsentAsync(table, identifier, newData).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public void remove(@NotNull String table, @NotNull String key) {
        removeAsync(table, key).awaitUninterruptedly();
    }

    @Override
    public void removeIfAbsent(@NotNull String table, @NotNull String identifier) {
        removeIfAbsentAsync(table, identifier).awaitUninterruptedly();
    }

    @Override
    public boolean createDatabase(@NotNull String name) {
        Boolean result = createDatabaseAsync(name).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean deleteDatabase(@NotNull String name) {
        Boolean result = deleteDatabaseAsync(name).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public boolean contains(@NotNull String table, @NotNull String key) {
        Boolean result = containsAsync(table, key).getUninterruptedly();
        return result == null ? false : result;
    }

    @Override
    public int size(@NotNull String table) {
        Integer result = sizeAsync(table).getUninterruptedly();
        return result == null ? 0 : result;
    }
}
