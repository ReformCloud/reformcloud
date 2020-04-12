package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mongo;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MongoDatabaseReader implements DatabaseReader {

    private static final String KEY_NAME = "_key";

    private static final String ID_NAME = "_identifier";

    MongoDatabaseReader(String table, Database<MongoDatabase> parent) {
        this.table = table;
        this.parent = parent;
    }

    private final String table;

    private final Database<MongoDatabase> parent;

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
            Document document = this.parent.get().getCollection(table).find(Filters.eq(ID_NAME, identifier)).first();
            if (document == null) {
                data.add(KEY_NAME, key).add(ID_NAME, identifier != null ? identifier : UUID.randomUUID().toString());
                this.parent.get().getCollection(table).insertOne(JsonConfiguration.GSON.get().fromJson(data.toPrettyString(), Document.class));
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
            Document document = this.parent.get().getCollection(table).find(Filters.eq(KEY_NAME, key)).first();
            if (document == null) {
                task.complete(false);
            } else {
                JsonConfiguration configuration = new JsonConfiguration(document.toJson());
                remove(key).awaitUninterruptedly();
                insert(key, configuration.getString(ID_NAME), newData).awaitUninterruptedly();
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
            Document document = this.parent.get().getCollection(table).find(Filters.eq(ID_NAME, identifier)).first();
            if (document == null) {
                task.complete(false);
            } else {
                JsonConfiguration configuration = new JsonConfiguration(document.toJson());
                remove(configuration.getString(KEY_NAME)).awaitUninterruptedly();
                insert(configuration.getString(KEY_NAME), identifier, newData).awaitUninterruptedly();
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
            this.parent.get().getCollection(table).deleteOne(Filters.eq(KEY_NAME, key));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> removeIfAbsent(@NotNull String identifier) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.parent.get().getCollection(table).deleteOne(Filters.eq(ID_NAME, identifier));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> contains(@NotNull String key) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Document document = this.parent.get().getCollection(table).find(Filters.eq(KEY_NAME, key)).first();
            task.complete(document != null);
        });
        return task;
    }

    @NotNull
    @Override
    public String getName() {
        return table;
    }

    @Override
    @NotNull
    public Iterator<JsonConfiguration> iterator() {
        List<JsonConfiguration> list = new ArrayList<>();
        this.parent.get().getCollection(table).find().forEach((Consumer<Document>) document -> list.add(new JsonConfiguration(document.toJson())));
        return list.iterator();
    }

    private Task<JsonConfiguration> get(String keyName, String expected) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Document document = this.parent.get().getCollection(table).find(Filters.eq(keyName, expected)).first();
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
