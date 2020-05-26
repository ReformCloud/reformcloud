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
package systems.reformcloud.reformcloud2.executor.api.database.basic.drivers.mongo;

import com.google.gson.Gson;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.database.Database;
import systems.reformcloud.reformcloud2.executor.api.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.task.defaults.DefaultTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MongoDatabaseReader implements DatabaseReader {

    private static final String KEY_NAME = "_key";

    private static final String ID_NAME = "_identifier";

    private static final Gson GSON = new JsonConfiguration().getGson();
    private final String table;
    private final Database<MongoDatabase> parent;

    MongoDatabaseReader(String table, Database<MongoDatabase> parent) {
        this.table = table;
        this.parent = parent;
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
    public Task<JsonConfiguration> insert(@NotNull String key, String identifier, @NotNull JsonConfiguration data) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Document document = this.parent.get().getCollection(this.table).find(Filters.eq(ID_NAME, identifier)).first();
            if (document == null) {
                data.add(KEY_NAME, key).add(ID_NAME, identifier != null ? identifier : UUID.randomUUID().toString());
                this.parent.get().getCollection(this.table).insertOne(GSON.fromJson(data.toPrettyString(), Document.class));
                task.complete(data);
            } else {
                task.complete(new JsonConfiguration(document.toJson()));
            }
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> update(@NotNull String key, @NotNull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Document document = this.parent.get().getCollection(this.table).find(Filters.eq(KEY_NAME, key)).first();
            if (document == null) {
                task.complete(false);
            } else {
                JsonConfiguration configuration = new JsonConfiguration(document.toJson());
                this.remove(key).awaitUninterruptedly();
                this.insert(key, configuration.getString(ID_NAME), newData).awaitUninterruptedly();
                task.complete(true);
            }
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> updateIfAbsent(@NotNull String identifier, @NotNull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Document document = this.parent.get().getCollection(this.table).find(Filters.eq(ID_NAME, identifier)).first();
            if (document == null) {
                task.complete(false);
            } else {
                JsonConfiguration configuration = new JsonConfiguration(document.toJson());
                this.remove(configuration.getString(KEY_NAME)).awaitUninterruptedly();
                this.insert(configuration.getString(KEY_NAME), identifier, newData).awaitUninterruptedly();
                task.complete(true);
            }
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> remove(@NotNull String key) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.parent.get().getCollection(this.table).deleteOne(Filters.eq(KEY_NAME, key));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> removeIfAbsent(@NotNull String identifier) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.parent.get().getCollection(this.table).deleteOne(Filters.eq(ID_NAME, identifier));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> contains(@NotNull String key) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Document document = this.parent.get().getCollection(this.table).find(Filters.eq(KEY_NAME, key)).first();
            task.complete(document != null);
        });
        return task;
    }

    @NotNull
    @Override
    public String getName() {
        return this.table;
    }

    @Override
    @NotNull
    public Iterator<JsonConfiguration> iterator() {
        List<JsonConfiguration> list = new ArrayList<>();
        this.parent.get().getCollection(this.table).find().forEach((Consumer<Document>) document -> list.add(new JsonConfiguration(document.toJson())));
        return list.iterator();
    }

    private Task<JsonConfiguration> get(String keyName, String expected) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Document document = this.parent.get().getCollection(this.table).find(Filters.eq(keyName, expected)).first();
            if (document == null) {
                task.complete(null);
            } else {
                JsonConfiguration configuration = new JsonConfiguration(document.toJson());
                configuration.remove(KEY_NAME).remove(ID_NAME);
                task.complete(configuration);
            }
        });
        return task;
    }
}
