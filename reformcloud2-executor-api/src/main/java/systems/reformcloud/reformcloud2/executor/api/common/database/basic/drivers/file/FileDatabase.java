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
package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.file;

import de.derklaro.projects.deer.api.provider.DatabaseProvider;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.AbsentMap;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

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
    public void connect(@NotNull String host, int port, @NotNull String userName, @NotNull String password, @NotNull String table) {
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
        return perTableReader.putIfAbsent(table, new FileDatabaseReader(this.table, table));
    }

    @NotNull
    @Override
    public Path get() {
        return Paths.get(table);
    }

    private void initDependencies() {
        URL url = DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.JITPACK,
                "com.github.derklaro",
                "project-deer",
                "1.0.0"
        ));
        Conditions.nonNull(url, StringUtil.formatError("dependency executor load for file database"));
        DEPENDENCY_LOADER.addDependency(url);
    }
}
