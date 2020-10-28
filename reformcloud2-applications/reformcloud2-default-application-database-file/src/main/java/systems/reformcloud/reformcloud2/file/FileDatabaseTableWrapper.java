/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.file;

import de.derklaro.projects.deer.api.Database;
import de.derklaro.projects.deer.api.basic.Filters;
import de.derklaro.projects.deer.api.provider.DatabaseProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.wrappers.DatabaseTableWrapper;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FileDatabaseTableWrapper implements DatabaseTableWrapper {

    private final Database<SerializableJsonConfiguration> database;

    public FileDatabaseTableWrapper(@NotNull String tableName) {
        this.database = DatabaseProvider.getDatabaseDriver().getDatabase(
            Paths.get("reformcloud/.database").resolve(tableName).toFile(),
            SerializableJsonConfiguration::new,
            1
        );
    }

    @Override
    public void insert(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration data) {
        Optional<SerializableJsonConfiguration> entry = this.getEntry(key, id);
        if (entry.isPresent()) {
            this.database.updateKey(Filters.keyEq(key), new SerializableJsonConfiguration(data));
        } else {
            this.database.insert(key, new String[]{id}, new SerializableJsonConfiguration(data));
        }
    }

    @Override
    public void update(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration newData) {
        Optional<SerializableJsonConfiguration> entry = this.getEntry(key, id);
        if (entry.isPresent()) {
            this.database.updateKey(Filters.keyEq(key), new SerializableJsonConfiguration(newData));
        } else {
            this.database.insert(key, new String[]{id}, new SerializableJsonConfiguration(newData));
        }
    }

    @Override
    public void remove(@NotNull String key, @NotNull String id) {
        this.database.delete(Filters.keyEq(key));
        this.database.delete(Filters.anyValueMatch(id));
    }

    @Override
    public @NotNull
    Optional<JsonConfiguration> get(@NotNull String key, @NotNull String id) {
        return this.getEntry(key, id).map(result -> new JsonConfiguration(result.getJsonObject()));
    }

    @Override
    public @NotNull
    @UnmodifiableView Collection<String> getEntryNames() {
        Collection<String> collection = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.database.getTargetFolder().toPath())) {
            for (Path path : stream) {
                if (path.toString().endsWith(".properties")) {
                    continue;
                }

                String fileName = path.getFileName().toString();
                collection.add(fileName.split("-")[0]);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return collection;
    }

    @Override
    public long count() {
        AtomicLong atomicLong = new AtomicLong();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.database.getTargetFolder().toPath())) {
            for (Path path : stream) {
                if (path.toString().endsWith(".properties")) {
                    continue;
                }

                atomicLong.addAndGet(1);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return atomicLong.get();
    }

    @Override
    public void clear() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.database.getTargetFolder().toPath())) {
            for (Path path : stream) {
                if (path.toString().endsWith(".properties")) {
                    continue;
                }

                IOUtils.deleteFile(path);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public @NotNull
    @UnmodifiableView Collection<JsonConfiguration> getAll() {
        Collection<JsonConfiguration> collection = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.database.getTargetFolder().toPath())) {
            for (Path path : stream) {
                if (path.toString().endsWith(".properties")) {
                    continue;
                }

                collection.add(JsonConfiguration.read(path));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return collection;
    }

    @Override
    public boolean has(@NotNull String key) {
        return this.database.getEntry(Filters.keyEq(key)).isPresent();
    }

    private Optional<SerializableJsonConfiguration> getEntry(String key, String id) {
        Optional<SerializableJsonConfiguration> entry = this.database.getEntry(Filters.keyEq(key));
        return entry.isPresent() ? entry : this.database.getEntry(Filters.anyValueMatch(id));
    }
}
