package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.file;

import de.derklaro.projects.deer.api.basic.Filters;
import de.derklaro.projects.deer.api.provider.DatabaseProvider;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.AbsentMap;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class FileDatabase extends Database<Path> {

    private String table;

    private final Map<String, DatabaseReader> perTableReader = new AbsentMap<>();

    public FileDatabase() {
        this.initDependencies();

        try {
            Class.forName("de.derklaro.projects.deer.executor.BasicDatabaseDriver");
        } catch (final ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void connect(@Nonnull String host, int port, @Nonnull String userName, @Nonnull String password, @Nonnull String table) {
        this.table = "reformcloud/.database/" + table;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void reconnect() {
    }

    @Override
    public void disconnect() {
    }

    @Override
    public boolean createDatabase(String name) {
        DatabaseProvider.getDatabaseDriver().getDatabase(new File(this.table, name), file -> null, 1);
        return true;
    }

    @Override
    public boolean deleteDatabase(String name) {
        DatabaseProvider.getDatabaseDriver().deleteDatabase(new File(this.table, name));
        perTableReader.remove(name);
        return true;
    }

    @Override
    public DatabaseReader createForTable(String table) {
        return perTableReader.putIfAbsent(table, new DatabaseReader() {

            private final de.derklaro.projects.deer.api.Database<SerializableJsonConfiguration> database = DatabaseProvider.getDatabaseDriver().getDatabase(
                    new File(FileDatabase.this.table, table),
                    SerializableJsonConfiguration::new,
                    1
            );

            @Nonnull
            @Override
            public Task<JsonConfiguration> find(@Nonnull String key) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> task.complete(database.getEntry(Filters.keyEq(key)).orElse(new SerializableJsonConfiguration())));
                return task;
            }

            @Nonnull
            @Override
            public Task<JsonConfiguration> findIfAbsent(@Nonnull String identifier) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> task.complete(database.getEntry(Filters.anyValueMatch(identifier)).orElse(new SerializableJsonConfiguration())));
                return task;
            }

            @Nonnull
            @Override
            public Task<JsonConfiguration> insert(@Nonnull String key, String identifier, @Nonnull JsonConfiguration data) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    Optional<SerializableJsonConfiguration> optional = database.getEntry(Filters.keyEq(key));
                    if (optional.isPresent()) {
                        task.complete(optional.get());
                        return;
                    }

                    database.insert(key, new String[]{identifier}, new SerializableJsonConfiguration(data));
                    task.complete(data);
                });
                return task;
            }

            @Nonnull
            @Override
            public Task<Boolean> update(@Nonnull String key, @Nonnull JsonConfiguration newData) {
                Task<Boolean> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    database.updateKey(Filters.keyEq(key), new SerializableJsonConfiguration(newData));
                    task.complete(true);
                });
                return task;
            }

            @Nonnull
            @Override
            public Task<Boolean> updateIfAbsent(@Nonnull String identifier, @Nonnull JsonConfiguration newData) {
                Task<Boolean> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    database.updateKey(Filters.anyValueMatch(identifier), new SerializableJsonConfiguration(newData));
                    task.complete(true);
                });
                return task;
            }

            @Nonnull
            @Override
            public Task<Void> remove(@Nonnull String key) {
                Task<Void> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    database.delete(Filters.keyEq(key));
                    task.complete(null);
                });
                return task;
            }

            @Nonnull
            @Override
            public Task<Void> removeIfAbsent(@Nonnull String identifier) {
                Task<Void> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    database.delete(Filters.anyValueMatch(identifier));
                    task.complete(null);
                });
                return task;
            }

            @Nonnull
            @Override
            public Task<Boolean> contains(@Nonnull String key) {
                Task<Boolean> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> task.complete(database.getEntry(Filters.keyEq(key)).isPresent()));
                return task;
            }

            @Nonnull
            @Override
            public Task<Integer> size() {
                Task<Integer> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    try {
                        AtomicInteger count = new AtomicInteger();
                        DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(FileDatabase.this.table + "/" + table));
                        stream.forEach(files -> {
                            if (!files.getFileName().toString().endsWith(".properties")) {
                                count.getAndIncrement();
                            }
                        });

                        stream.close();
                        task.complete(count.intValue());
                    } catch (final IOException ex) {
                        ex.printStackTrace();
                    }
                });
                return task;
            }

            @Nonnull
            @Override
            public String getName() {
                return table;
            }

            @Override
            @Nonnull
            public Iterator<JsonConfiguration> iterator() {
                List<JsonConfiguration> list = new ArrayList<>();
                for (File file : Objects.requireNonNull(new File(FileDatabase.this.table + "/" + table).listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".json")))) {
                    list.add(JsonConfiguration.read(file));
                }

                return list.iterator();
            }
        });
    }

    @Nonnull
    @Override
    public Path get() {
        return Paths.get(table);
    }

    private void initDependencies() {
        URL url = DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.REFORMCLOUD,
                "de.derklaro.projects.deer",
                "project-deer-executor",
                "1.0-SNAPSHOT"
        ));
        Conditions.nonNull(url, StringUtil.formatError("dependency executor load for file database"));
        DEPENDENCY_LOADER.addDependency(url);

        URL apiUrl = DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.REFORMCLOUD,
                "de.derklaro.projects.deer",
                "project-deer-api",
                "1.0-SNAPSHOT"
        ));
        Conditions.nonNull(apiUrl, StringUtil.formatError("dependency api load for file database"));
        DEPENDENCY_LOADER.addDependency(apiUrl);
    }
}
