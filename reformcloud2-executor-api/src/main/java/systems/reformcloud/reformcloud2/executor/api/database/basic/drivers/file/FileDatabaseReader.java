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
package systems.reformcloud.reformcloud2.executor.api.database.basic.drivers.file;

import de.derklaro.projects.deer.api.Database;
import de.derklaro.projects.deer.api.basic.Filters;
import de.derklaro.projects.deer.api.provider.DatabaseProvider;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.task.defaults.DefaultTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileDatabaseReader implements DatabaseReader {

    private final String parentTable;
    private final String table;
    private final Database<SerializableJsonConfiguration> database;

    FileDatabaseReader(String parentTable, String table) {
        this.parentTable = parentTable;
        this.table = table;
        this.database = DatabaseProvider.getDatabaseDriver().getDatabase(
                new File(parentTable, table),
                SerializableJsonConfiguration::new,
                1
        );
    }

    @NotNull
    @Override
    public Task<JsonConfiguration> find(@NotNull String key) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(this.database.getEntry(Filters.keyEq(key)).orElse(new SerializableJsonConfiguration())));
        return task;
    }

    @NotNull
    @Override
    public Task<JsonConfiguration> findIfAbsent(@NotNull String identifier) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(this.database.getEntry(Filters.anyValueMatch(identifier)).orElse(new SerializableJsonConfiguration())));
        return task;
    }

    @NotNull
    @Override
    public Task<JsonConfiguration> insert(@NotNull String key, String identifier, @NotNull JsonConfiguration data) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Optional<SerializableJsonConfiguration> optional = this.database.getEntry(Filters.keyEq(key));
            if (optional.isPresent()) {
                task.complete(optional.get());
                return;
            }

            this.database.insert(key, new String[]{identifier}, new SerializableJsonConfiguration(data));
            task.complete(data);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> update(@NotNull String key, @NotNull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.database.updateKey(Filters.keyEq(key), new SerializableJsonConfiguration(newData));
            task.complete(true);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> updateIfAbsent(@NotNull String identifier, @NotNull JsonConfiguration newData) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.database.updateKey(Filters.anyValueMatch(identifier), new SerializableJsonConfiguration(newData));
            task.complete(true);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> remove(@NotNull String key) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.database.delete(Filters.keyEq(key));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> removeIfAbsent(@NotNull String identifier) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            this.database.delete(Filters.anyValueMatch(identifier));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Boolean> contains(@NotNull String key) {
        Task<Boolean> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> task.complete(this.database.getEntry(Filters.keyEq(key)).isPresent()));
        return task;
    }

    @NotNull
    @Override
    public Task<Integer> size() {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(this.parentTable + "/" + this.table))) {
                AtomicInteger count = new AtomicInteger();
                stream.forEach(files -> {
                    if (!files.getFileName().toString().endsWith(".properties")) {
                        count.getAndIncrement();
                    }
                });

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
        return this.table;
    }

    @Override
    @NotNull
    public Iterator<JsonConfiguration> iterator() {
        List<JsonConfiguration> list = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(this.parentTable + "/" + this.table).listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".json")))) {
            list.add(JsonConfiguration.read(file));
        }

        return list.iterator();
    }

}
