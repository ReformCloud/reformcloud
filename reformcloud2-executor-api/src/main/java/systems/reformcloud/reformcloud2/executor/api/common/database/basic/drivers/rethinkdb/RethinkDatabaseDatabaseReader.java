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
package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.rethinkdb;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class RethinkDatabaseDatabaseReader implements DatabaseReader {

    private static final String KEY_NAME = "_key";

    private static final String ID_NAME = "_identifier";
    private final Database<RethinkDB> parent;
    private final Connection connection;
    private final String table;

    public RethinkDatabaseDatabaseReader(Database<RethinkDB> parent, Connection connection, String table) {
        this.parent = parent;
        this.connection = connection;
        this.table = table;
    }

    @NotNull
    @Override
    public Task<JsonConfiguration> find(@NotNull String key) {
        return this.get(KEY_NAME, key);
    }

    @NotNull
    @Override
    public Task<JsonConfiguration> findIfAbsent(@NotNull String identifier) {
        return this.get(ID_NAME, identifier);
    }

    @NotNull
    @Override
    public Task<JsonConfiguration> insert(@NotNull String key, @Nullable String identifier, @NotNull JsonConfiguration data) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.parent.get().table(this.table).insert(this.asMap(key, identifier, data)).run(this.connection);
            task.complete(data);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> update(@NotNull String key, @NotNull JsonConfiguration newData) {
        return this.update(KEY_NAME, key, newData);
    }

    @NotNull
    @Override
    public Task<Boolean> updateIfAbsent(@NotNull String identifier, @NotNull JsonConfiguration newData) {
        return this.update(ID_NAME, identifier, newData);
    }

    @NotNull
    @Override
    public Task<Void> remove(@NotNull String key) {
        return this.delete(KEY_NAME, key);
    }

    @NotNull
    @Override
    public Task<Void> removeIfAbsent(@NotNull String identifier) {
        return this.delete(ID_NAME, identifier);
    }

    @NotNull
    @Override
    public Task<Boolean> contains(@NotNull String key) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(this.find(key).getUninterruptedly() != null));
        return task;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Iterator<JsonConfiguration> iterator() {
        return this.parent
                .get()
                .table(this.table)
                .run(this.connection)
                .stream()
                .filter(e -> e instanceof Map)
                .map(e -> new JsonConfiguration(((Map<String, String>) e).get("values")))
                .collect(Collectors.toList())
                .iterator();
    }

    @NotNull
    @Override
    public String getName() {
        return this.table;
    }

    private Task<JsonConfiguration> get(String keyName, String expected) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Map<String, String> map = this.getMapObject(keyName, expected).getUninterruptedly();
            if (map != null) {
                task.complete(new JsonConfiguration(map.get("values")));
                return;
            }

            task.complete(null);
        });
        return task;
    }

    @SuppressWarnings("unchecked")
    private Task<Map<String, String>> getMapObject(String keyName, String expected) {
        Task<Map<String, String>> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Result<Object> result = this.parent
                    .get()
                    .table(this.table)
                    .filter(this.parent.get().hashMap(keyName, expected))
                    .run(this.connection);
            if (result.hasNext()) {
                Object next = result.first();
                if (next instanceof Map) {
                    task.complete((Map<String, String>) next);
                    return;
                }
            }

            task.complete(null);
            result.close();
        });
        return task;
    }

    private Task<Boolean> update(String keyName, String expected, JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Map<String, String> map = this.getMapObject(keyName, expected).getUninterruptedly();
            if (map != null) {
                this.delete(keyName, expected).awaitUninterruptedly();
                this.insert(map.get(KEY_NAME), map.get(ID_NAME), newData).awaitUninterruptedly();
            }

            task.complete(map != null);
        });
        return task;
    }

    private Task<Void> delete(String keyName, String expected) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.parent.get().table(this.table).filter(this.parent.get().hashMap(keyName, expected)).delete().run(this.connection);
            task.complete(null);
        });
        return task;
    }

    private MapObject<Object, Object> asMap(String key, String identifier, JsonConfiguration configuration) {
        return this.parent.get()
                .hashMap()
                .with(KEY_NAME, key == null ? "" : key)
                .with(ID_NAME, identifier == null ? "" : identifier)
                .with("values", configuration.toPrettyString());
    }
}
