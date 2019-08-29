package de.klaro.reformcloud2.executor.api.common.database.basic.drivers.mysql;

import de.klaro.reformcloud2.executor.api.common.database.Database;
import de.klaro.reformcloud2.executor.api.common.dependency.DefaultDependency;
import de.klaro.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

public final class MySQLDatabase extends Database<Connection> {

    private static final String CONNECT_ARGUMENTS = "jdbc:mysql://{0}:{1}/{2}?autoReconnect=true&useUnicode=true&useJDBCCompliantTimezoneShift=true" +
            "&useLegacyDatetimeCode=false&serverTimezone=UTC";

    public MySQLDatabase() {
        Properties properties = new Properties();
        properties.setProperty("mysql-connector-java", "8.0.17");

        DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "mysql",
                "mysql-connector-java",
                properties
        ));
    }

    private String host;

    private int port;

    private String userName;

    private String password;

    private String table;

    private Connection connection;

    @Override
    public void connect(String host, int port, String userName, String password, String table) {
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
    public Connection get() {
        return connection;
    }
}
