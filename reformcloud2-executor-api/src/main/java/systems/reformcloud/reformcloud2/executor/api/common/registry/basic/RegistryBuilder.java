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
package systems.reformcloud.reformcloud2.executor.api.common.registry.basic;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.registry.Registry;
import systems.reformcloud.reformcloud2.executor.api.common.utility.system.SystemHelper;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public final class RegistryBuilder {

    private RegistryBuilder() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Registry newRegistry(@NotNull Path operatingFolder) {
        return new RegistryImpl(operatingFolder);
    }

    private static final class RegistryImpl implements Registry {

        private final Path folder;

        private RegistryImpl(Path folder) {
            this.folder = folder;
            SystemHelper.createDirectory(folder);
        }

        @NotNull
        @Override
        public <T> T createKey(@NotNull String keyName, @NotNull T t) {
            Path target = Paths.get(this.folder + "/" + keyName + ".json");
            if (Files.exists(target)) {
                return t;
            }

            new JsonConfiguration().add("key", t).write(target);
            return t;
        }

        @Override
        public <T> T getKey(@NotNull String keyName) {
            Path target = Paths.get(this.folder + "/" + keyName + ".json");
            if (Files.exists(target)) {
                return null;
            }

            return JsonConfiguration.read(target).get("key", new TypeToken<T>() {
            });
        }

        @Override
        public void deleteKey(@NotNull String key) {
            Path target = Paths.get(this.folder + "/" + key + ".json");
            SystemHelper.deleteFile(target.toFile());
        }

        @Override
        public <T> T updateKey(@NotNull String key, @NotNull T newValue) {
            Path target = Paths.get(this.folder + "/" + key + ".json");
            if (!Files.exists(target)) {
                return null;
            }

            new JsonConfiguration()
                    .add("key", newValue)
                    .write(target);
            return newValue;
        }

        @NotNull
        @Override
        public <T> Collection<T> readKeys(@NotNull Function<JsonConfiguration, T> function) {
            if (!Files.exists(this.folder)) {
                return Collections.emptyList();
            }

            final Collection<T> out = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.folder)) {
                for (Path path : stream) {
                    out.add(function.apply(JsonConfiguration.read(path)));
                }
            } catch (final IOException exception) {
                exception.printStackTrace();
            }

            return out;
        }
    }
}
