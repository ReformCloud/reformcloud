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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.executor.api.provider.DatabaseProvider;
import systems.reformcloud.reformcloud2.executor.api.wrappers.DatabaseTableWrapper;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public class FileDatabaseProvider implements DatabaseProvider {

    public FileDatabaseProvider() {
        try {
            Class.forName("de.derklaro.projects.deer.executor.BasicDatabaseDriver");
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public @NotNull
    DatabaseTableWrapper createTable(@NotNull String tableName) {
        return new FileDatabaseTableWrapper(tableName);
    }

    @Override
    public void deleteTable(@NotNull String tableName) {
        IOUtils.deleteDirectory(Paths.get("reformcloud/.database", tableName));
    }

    @Override
    public @NotNull
    @UnmodifiableView Collection<String> getTableNames() {
        Collection<String> collection = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("reformcloud/.database"))) {
            for (Path path : stream) {
                collection.add(path.getFileName().toString());
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return collection;
    }

    @Override
    public @NotNull
    DatabaseTableWrapper getDatabase(@NotNull String tableName) {
        return new FileDatabaseTableWrapper(tableName);
    }
}
