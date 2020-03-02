package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mysql;

import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.database.sql.SQLDatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.common.scheduler.TaskScheduler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.AbsentMap;

import javax.annotation.Nonnull;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public final class MySQLDatabase extends Database<Connection> {

    private final Map<String, DatabaseReader> perTableReader = new AbsentMap<>();

    private static final String CONNECT_ARGUMENTS = "jdbc:mysql://{0}:{1}/{2}?autoReconnect=true&useUnicode=true&useJDBCCompliantTimezoneShift=true" +
            "&useLegacyDatetimeCode=false&serverTimezone=UTC";

    public MySQLDatabase() {
        Properties properties = new Properties();
        properties.setProperty("mysql-connector-java", "8.0.19");

        URL dependency = DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "mysql",
                "mysql-connector-java",
                properties
        ));
        Conditions.nonNull(dependency, StringUtil.formatError("dependency load for MySQL database"));
        DEPENDENCY_LOADER.addDependency(dependency);
    }

    private String host;

    private int port;

    private String userName;

    private String password;

    private String table;

    private Connection connection;

    @Override
    public void connect(@Nonnull String host, int port, @Nonnull String userName, @Nonnull String password, @Nonnull String table) {
        if (!isConnected()) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.table = table;

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

                this.connection = DriverManager.getConnection(
                        MessageFormat.format(
                                CONNECT_ARGUMENTS,
                                host,
                                Integer.toString(port),
                                table
                        ), userName, password
                );

                if (isConnected()) {
                    startReconnect();
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
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
        connect(host, port, userName, password, table);
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (final SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public boolean createDatabase(String name) {
        try (PreparedStatement statement = SQLDatabaseReader.prepareStatement("CREATE TABLE IF NOT EXISTS `"
                + name + "` (`key` TEXT, `identifier` TEXT, `data` LONGBLOB);", this)) {
            statement.executeUpdate();
            return true;
        } catch (final SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDatabase(String name) {
        try (PreparedStatement statement = SQLDatabaseReader.prepareStatement("DROP TABLE `" + name + "`", this)) {
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

    @Nonnull
    @Override
    public Connection get() {
        return connection;
    }

    private void startReconnect() {
        TaskScheduler.INSTANCE.schedule(() -> createDatabase("internal_users"), 0, 5, TimeUnit.MINUTES);
    }
}
