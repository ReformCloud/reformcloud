package de.klaro.reformcloud2.executor.api.common.database.basic.drivers.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.database.Database;
import de.klaro.reformcloud2.executor.api.common.database.DatabaseReader;
import de.klaro.reformcloud2.executor.api.common.dependency.DefaultDependency;
import de.klaro.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import de.klaro.reformcloud2.executor.api.common.utility.task.Task;
import de.klaro.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import org.bson.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class MongoDatabase extends Database<com.mongodb.client.MongoDatabase> {

    private final Map<String, DatabaseReader> perTableReader = new ConcurrentHashMap<>();

    public MongoDatabase() {
        Properties properties = new Properties();
        properties.setProperty("mongo-java-driver", "3.11.0");

        DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "org.mongodb",
                "mongo-java-driver",
                properties
        ));
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
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        Document document = mongoDatabase.getCollection(table).find(Filters.eq("key", key)).first();
                        if (document == null) {
                            task.complete(null);
                        } else {
                            task.complete(new JsonConfiguration(document.toJson()));
                        }
                    }
                });
                return task;
            }

            @Override
            public Task<JsonConfiguration> findIfAbsent(String identifier) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        Document document = mongoDatabase.getCollection(table).find(Filters.eq("identifier", identifier)).first();
                        if (document == null) {
                            task.complete(null);
                        } else {
                            task.complete(new JsonConfiguration(document.toJson()));
                        }
                    }
                });
                return task;
            }

            @Override
            public Task<JsonConfiguration> insert(String key, String identifier, JsonConfiguration data) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        Document document = mongoDatabase.getCollection(table).find(Filters.eq("identifier", identifier)).first();
                        if (document == null) {
                            mongoDatabase.getCollection(table).insertOne(JsonConfiguration.GSON.get().fromJson(data.toPrettyString(), Document.class));
                            task.complete(data);
                        } else {
                            task.complete(new JsonConfiguration(document.toJson()));
                        }
                    }
                });
                return task;
            }

            @Override
            public Task<Void> remove(String key, String identifier) {
                Task<Void> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        mongoDatabase.getCollection(table).deleteOne(Filters.eq("key", key));
                        task.complete(null);
                    }
                });
                return task;
            }

            @Override
            public Task<Void> removeIfAbsent(String identifier) {
                Task<Void> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        mongoDatabase.getCollection(table).deleteOne(Filters.eq("identifier", identifier));
                        task.complete(null);
                    }
                });
                return task;
            }

            @Override
            public Task<Boolean> contains(String key) {
                Task<Boolean> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        Document document = mongoDatabase.getCollection(table).find(Filters.eq("key", key)).first();
                        task.complete(document != null);
                    }
                });
                return task;
            }

            @Override
            public Task<Integer> size() {
                Task<Integer> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        AtomicInteger atomicInteger = new AtomicInteger(0);
                        mongoDatabase.getCollection(table).find().forEach(new Consumer<Document>() {
                            @Override
                            public void accept(Document document) {
                                atomicInteger.addAndGet(1);
                            }
                        });
                        task.complete(atomicInteger.get());
                    }
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
                mongoDatabase.getCollection(table).find().forEach(new Consumer<Document>() {
                    @Override
                    public void accept(Document document) {
                        list.add(new JsonConfiguration(document.toJson()));
                    }
                });
                return list.iterator();
            }
        });
    }

    @Override
    public com.mongodb.client.MongoDatabase get() {
        return mongoDatabase;
    }
}
