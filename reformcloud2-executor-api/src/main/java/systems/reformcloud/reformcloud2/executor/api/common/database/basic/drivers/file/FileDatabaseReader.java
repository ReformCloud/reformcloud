package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.file;

import de.derklaro.projects.deer.api.Database;
import de.derklaro.projects.deer.api.basic.Filters;
import de.derklaro.projects.deer.api.provider.DatabaseProvider;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileDatabaseReader implements DatabaseReader {

     FileDatabaseReader(String parentTable, String table) {
        this.parentTable = parentTable;
        this.table = table;
        this.database = DatabaseProvider.getDatabaseDriver().getDatabase(
                new File(parentTable, table),
                SerializableJsonConfiguration::new,
                1
        );
    }

    private final String parentTable;

    private final String table;

    private final Database<SerializableJsonConfiguration> database;

    @NotNull
    @Override
    public Task<JsonConfiguration> find(@NotNull String key) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(database.getEntry(Filters.keyEq(key)).orElse(new SerializableJsonConfiguration())));
        return task;
    }

    @NotNull
    @Override
    public Task<JsonConfiguration> findIfAbsent(@NotNull String identifier) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(database.getEntry(Filters.anyValueMatch(identifier)).orElse(new SerializableJsonConfiguration())));
        return task;
    }

    @NotNull
    @Override
    public Task<JsonConfiguration> insert(@NotNull String key, String identifier, @NotNull JsonConfiguration data) {
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

    @NotNull
    @Override
    public Task<Boolean> update(@NotNull String key, @NotNull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            database.updateKey(Filters.keyEq(key), new SerializableJsonConfiguration(newData));
            task.complete(true);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> updateIfAbsent(@NotNull String identifier, @NotNull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            database.updateKey(Filters.anyValueMatch(identifier), new SerializableJsonConfiguration(newData));
            task.complete(true);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> remove(@NotNull String key) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            database.delete(Filters.keyEq(key));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> removeIfAbsent(@NotNull String identifier) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            database.delete(Filters.anyValueMatch(identifier));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> contains(@NotNull String key) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(database.getEntry(Filters.keyEq(key)).isPresent()));
        return task;
    }

    @NotNull
    @Override
    public Task<Integer> size() {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            try {
                AtomicInteger count = new AtomicInteger();
                DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(this.parentTable + "/" + table));
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

    @NotNull
    @Override
    public String getName() {
        return table;
    }

    @Override
    @NotNull
    public Iterator<JsonConfiguration> iterator() {
        List<JsonConfiguration> list = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(this.parentTable + "/" + table).listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".json")))) {
            list.add(JsonConfiguration.read(file));
        }

        return list.iterator();
    }

}
