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
package systems.reformcloud.reformcloud2.executor.api.database.basic.drivers.h2;

import org.h2.Driver;
import org.h2.store.fs.FileUtils;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.database.Database;
import systems.reformcloud.reformcloud2.executor.api.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.database.sql.SQLDatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.utility.maps.AbsentMap;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class H2Database extends Database<Connection> {

    private final Map<String, DatabaseReader> perTableReader = new AbsentMap<>();
    private ReformCloudWrappedConnection connection;

    public H2Database() {
        URL url = DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "com.h2database",
                "h2",
                "1.4.200"
        ));
        Conditions.nonNull(url, StringUtil.formatError("dependency load for h2 database"));
        DEPENDENCY_LOADER.addDependency(url);

        FileUtils.createDirectories("reformcloud/.database/h2");
    }

    @Override
    public void connect(@NotNull String host, int port, @NotNull String userName, @NotNull String password, @NotNull String table) {
        if (!this.isConnected()) {
            try {
                Driver.load();

                Connection connection = DriverManager.getConnection("jdbc:h2:" + new File("reformcloud/.database/h2/h2_db").getAbsolutePath());
                this.connection = new ReformCloudWrappedConnection(connection);
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return this.connection != null && !this.connection.isClosed() && this.connection.isValid(250);
        } catch (final SQLException ex) {
            return this.connection != null;
        }
    }

    @Override
    public void disconnect() {
        if (this.isConnected()) {
            try {
                this.connection.disconnect();
            } catch (final SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean createDatabase(String name) {
        try (PreparedStatement statement = SQLDatabaseReader.prepareStatement("CREATE TABLE IF NOT EXISTS `"
                + name + "` (`key` TEXT, `identifier` TEXT, `data` LONGBLOB);", this.connection)) {
            statement.executeUpdate();
            return true;
        } catch (final SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDatabase(String name) {
        try (PreparedStatement statement = SQLDatabaseReader.prepareStatement("DROP TABLE `" + name + "`", this.connection)) {
            statement.executeUpdate();
            return true;
        } catch (final SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public DatabaseReader createForTable(String table) {
        this.createDatabase(table);
        return this.perTableReader.putIfAbsent(table, new SQLDatabaseReader(table, this));
    }

    @NotNull
    @Override
    public Connection get() {
        return this.connection;
    }
}
