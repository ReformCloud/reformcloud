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
package systems.reformcloud.reformcloud2.executor.node.api.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.database.DatabaseSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.concurrent.TimeUnit;
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
    public Task<Void> updateAsync(@NotNull String table, @Nullable String key, @Nullable String identifier, @NotNull JsonConfiguration newData) {
        return Task.supply(() -> {
            DatabaseReader reader = this.database.createForTable(table);
            if (reader == null) {
                return null;
            }

            if (key != null) {
                Boolean success = reader.update(key, newData).getUninterruptedly(TimeUnit.SECONDS, 5);
                if (success != null && success) {
                    return null;
                }
            }

            if (identifier != null) {
                reader.updateIfAbsent(identifier, newData);
            }

            return null;
        });
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
    public Task<Void> removeAsync(@NotNull String table, @Nullable String key, @Nullable String identifier) {
        return Task.supply(() -> {
            DatabaseReader reader = this.database.createForTable(table);
            if (reader == null) {
                return null;
            }

            if (key != null) {
                reader.remove(key);
            }

            if (identifier != null) {
                reader.removeIfAbsent(identifier);
            }

            return null;
        });
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
    public void update(@NotNull String table, @Nullable String key, @Nullable String identifier, @NotNull JsonConfiguration newData) {
        this.updateAsync(table, key, identifier, newData).awaitUninterruptedly();
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
    public void remove(@NotNull String table, @Nullable String key, @Nullable String identifier) {
        this.removeAsync(table, key, identifier).awaitUninterruptedly();
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
