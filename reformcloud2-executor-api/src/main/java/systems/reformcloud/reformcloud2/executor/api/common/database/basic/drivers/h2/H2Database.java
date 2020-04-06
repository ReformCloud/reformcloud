package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.h2;

import org.h2.Driver;
import org.h2.store.fs.FileUtils;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.database.sql.SQLDatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.AbsentMap;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class H2Database extends Database<Connection> {

    private final Map<String, DatabaseReader> perTableReader = new AbsentMap<>();

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

    private ReformCloudWrappedConnection connection;

    @Override
    public void connect(@NotNull String host, int port, @NotNull String userName, @NotNull String password, @NotNull String table) {
        if (!isConnected()) {
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
            return connection != null && !connection.isClosed() && connection.isValid(250);
        } catch (final SQLException ex) {
            return connection != null;
        }
    }

    @Override
    public void reconnect() {
        disconnect();
        connect("", -1, "", "", "");
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
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
        return perTableReader.putIfAbsent(table, new SQLDatabaseReader(table, this));
    }

    @NotNull
    @Override
    public Connection get() {
        return connection;
    }
}
