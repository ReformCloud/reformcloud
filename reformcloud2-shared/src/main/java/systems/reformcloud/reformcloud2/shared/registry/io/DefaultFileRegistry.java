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
package systems.reformcloud.reformcloud2.shared.registry.io;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.registry.io.FileRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class DefaultFileRegistry implements FileRegistry {

    public DefaultFileRegistry(String operatingFolder) {
        this.operatingFolder = operatingFolder;
        IOUtils.createDirectory(Paths.get(operatingFolder));
    }

    private final String operatingFolder;

    @NotNull
    @Override
    public <T> T createKey(@NotNull String keyName, @NotNull T t) {
        Path filePath = Paths.get(this.operatingFolder, keyName + ".json");
        if (Files.exists(filePath)) {
            return t;
        }

        new JsonConfiguration().add("key", t).write(filePath);
        return t;
    }

    @NotNull
    @Override
    public <T> Optional<T> getKey(@NotNull String keyName) {
        Path filePath = Paths.get(this.operatingFolder, keyName + ".json");
        if (Files.notExists(filePath)) {
            return Optional.empty();
        }

        return Optional.ofNullable(JsonConfiguration.read(filePath).get("key", new TypeToken<T>() {
        }));
    }

    @Override
    public void deleteKey(@NotNull String key) {
        IOUtils.deleteFile(new File(this.operatingFolder, key + ".json"));
    }

    @NotNull
    @Override
    public <T> Optional<T> updateKey(@NotNull String key, @NotNull T newValue) {
        Path filePath = Paths.get(this.operatingFolder, key + ".json");
        if (Files.notExists(filePath)) {
            return Optional.empty();
        }

        new JsonConfiguration().add("key", newValue).write(filePath);
        return Optional.of(newValue);
    }

    @NotNull
    @Override
    public <T> Collection<T> readKeys(@NotNull Function<JsonConfiguration, T> function) {
        Collection<T> result = new CopyOnWriteArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(this.operatingFolder), path -> path.toString().endsWith(".json"))) {
            for (Path path : stream) {
                result.add(function.apply(JsonConfiguration.read(path)));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return result;
    }
}
