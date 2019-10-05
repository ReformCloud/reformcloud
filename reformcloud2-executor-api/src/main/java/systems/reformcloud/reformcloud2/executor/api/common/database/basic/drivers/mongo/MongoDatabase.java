package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.AbsentMap;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class MongoDatabase extends Database<com.mongodb.client.MongoDatabase> {

    private final Map<String, DatabaseReader> perTableReader = new AbsentMap<>();

    public MongoDatabase() {
        Properties properties = new Properties();
        properties.setProperty("mongo-java-driver", "3.11.0");

        URL dependency = DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "org.mongodb",
                "mongo-java-driver",
                properties
        ));
        DEPENDENCY_LOADER.addDependency(dependency);
    }

    private MongoClient mongoClient;

    private com.mongodb.client.MongoDatabase mongoDatabase;

    private String host;

    private int port;

    private String userName;

    private String password;

    private String table;

    @Override
    public void connect(String host, int port, String userName, String password, String table) {
        if (!isConnected()) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.table = table;

            try {
                this.mongoClient = MongoClients.create(
                        MessageFormat.format(
                                "mongodb://{0}:{1}@{2}:{3}/{4}",
                                userName,
                                URLEncoder.encode(password, StandardCharsets.UTF_8.name()),
                                host,
                                Integer.toString(port),
                                table
                        )
                );
                this.mongoDatabase = mongoClient.getDatabase(table);
            } catch (final UnsupportedEncodingException ex) {
                ex.printStackTrace(); //Should never happen
            }
        }
    }

    @Override
    public boolean isConnected() {
        return mongoClient != null;
    }

    @Override
    public void reconnect() {
        disconnect();
        connect(host, port, userName, password, table);
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            this.mongoClient.close();
            this.mongoClient = null;
        }
    }

    @Override
    public boolean createDatabase(String name) {
        mongoDatabase.getCollection(name);
        return true;
    }

    @Override
    public boolean deleteDatabase(String name) {
        mongoDatabase.getCollection(name).drop();
        return true;
    }

    @Override
    public DatabaseReader createForTable(String table) {
        return perTableReader.putIfAbsent(table, new DatabaseReader() {
            @Override
            public Task<JsonConfiguration> find(String key) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    Document document = mongoDatabase.getCollection(table).find(Filters.eq("%%%%key", key)).first();
                    if (document == null) {
                        task.complete(null);
                    } else {
                        JsonConfiguration configuration = new JsonConfiguration(document.toJson());
                        configuration.remove("%%%%key").remove("%%%%identifier");
                        task.complete(configuration);
                    }
                });
                return task;
            }

            @Override
            public Task<JsonConfiguration> findIfAbsent(String identifier) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    Document document = mongoDatabase.getCollection(table).find(Filters.eq("%%%%identifier", identifier)).first();
                    if (document == null) {
                        task.complete(null);
                    } else {
                        JsonConfiguration configuration = new JsonConfiguration(document.toJson());
                        configuration.remove("%%%%key").remove("%%%%identifier");
                        task.complete(configuration);
                    }
                });
                return task;
            }

            @Override
            public Task<JsonConfiguration> insert(String key, String identifier, JsonConfiguration data) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    Document document = mongoDatabase.getCollection(table).find(Filters.eq("%%%%identifier", identifier)).first();
                    if (document == null) {
                        data.add("%%%%key", key).add("%%%%identifier", identifier != null ? identifier : UUID.randomUUID().toString());
                        mongoDatabase.getCollection(table).insertOne(JsonConfiguration.GSON.get().fromJson(data.toPrettyString(), Document.class));
                        task.complete(data);
                    } else {
                        task.complete(new JsonConfiguration(document.toJson()));
                    }
                });
                return task;
            }

            @Override
            public Task<Boolean> update(String key, JsonConfiguration newData) {
                Task<Boolean> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    Document document = mongoDatabase.getCollection(table).find(Filters.eq("%%%%key", key)).first();
                    if (document == null) {
                        task.complete(false);
                    } else {
                        JsonConfiguration configuration = new JsonConfiguration(document.toJson());
                        remove(key).awaitUninterruptedly();
                        insert(key, configuration.getString("%%%%identifier"), newData).awaitUninterruptedly();
                        task.complete(true);
                    }
                });
                return task;
            }

            @Override
            public Task<Boolean> updateIfAbsent(String identifier, JsonConfiguration newData) {
                Task<Boolean> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    Document document = mongoDatabase.getCollection(table).find(Filters.eq("%%%%identifier", identifier)).first();
                    if (document == null) {
                        task.complete(false);
                    } else {
                        JsonConfiguration configuration = new JsonConfiguration(document.toJson());
                        remove(configuration.getString("%%%%key")).awaitUninterruptedly();
                        insert(configuration.getString("%%%%key"), identifier, newData).awaitUninterruptedly();
                        task.complete(true);
                    }
                });
                return task;
            }

            @Override
            public Task<Void> remove(String key) {
                Task<Void> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    mongoDatabase.getCollection(table).deleteOne(Filters.eq("%%%%key", key));
                    task.complete(null);
                });
                return task;
            }

            @Override
            public Task<Void> removeIfAbsent(String identifier) {
                Task<Void> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    mongoDatabase.getCollection(table).deleteOne(Filters.eq("%%%%identifier", identifier));
                    task.complete(null);
                });
                return task;
            }

            @Override
            public Task<Boolean> contains(String key) {
                Task<Boolean> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    Document document = mongoDatabase.getCollection(table).find(Filters.eq("%%%%key", key)).first();
                    task.complete(document != null);
                });
                return task;
            }

            @Override
            public Task<Integer> size() {
                Task<Integer> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    AtomicInteger atomicInteger = new AtomicInteger(0);
                    mongoDatabase.getCollection(table).find().forEach((Consumer<Document>) document -> atomicInteger.addAndGet(1));
                    task.complete(atomicInteger.get());
                });
                return task;
            }

            @Override
            public String getName() {
                return table;
            }

            @Override
            public Iterator<JsonConfiguration> iterator() {
                List<JsonConfiguration> list = new ArrayList<>();
                mongoDatabase.getCollection(table).find().forEach((Consumer<Document>) document -> list.add(new JsonConfiguration(document.toJson())));
                return list.iterator();
            }
        });
    }

    @Override
    public com.mongodb.client.MongoDatabase get() {
        return mongoDatabase;
    }
}
