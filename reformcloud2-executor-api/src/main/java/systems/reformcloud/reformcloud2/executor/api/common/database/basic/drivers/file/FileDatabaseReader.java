package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.file;

import de.derklaro.projects.deer.api.Database;
import de.derklaro.projects.deer.api.basic.Filters;
import de.derklaro.projects.deer.api.provider.DatabaseProvider;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import javax.annotation.Nonnull;
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

    @Nonnull
    @Override
    public String getName() {
        return table;
    }

    @Override
    @Nonnull
    public Iterator<JsonConfiguration> iterator() {
        List<JsonConfiguration> list = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(this.parentTable + "/" + table).listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".json")))) {
            list.add(JsonConfiguration.read(file));
        }

        return list.iterator();
    }

}
