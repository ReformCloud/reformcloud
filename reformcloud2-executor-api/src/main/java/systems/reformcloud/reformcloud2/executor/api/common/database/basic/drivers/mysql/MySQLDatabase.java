package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mysql;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.common.scheduler.TaskScheduler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.AbsentMap;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"
                    + name + "` (`key` TEXT, `identifier` TEXT, `data` LONGBLOB);");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (final SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteDatabase(String name) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE `" + name + "`");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (final SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public DatabaseReader createForTable(String table) {
        return perTableReader.putIfAbsent(table, new DatabaseReader() {
            @Nonnull
            @Override
            public Task<JsonConfiguration> find(@Nonnull String key) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    try {
                        PreparedStatement statement = connection.prepareStatement("SELECT `data` FROM `" + table + "` WHERE `key` = ?");
                        statement.setString(1, key);
                        ResultSet resultSet = statement.executeQuery();
                        if (resultSet.next()) {
                            byte[] bytes = resultSet.getBytes("data");
                            if (bytes.length != 0) {
                                try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                                    task.complete(new JsonConfiguration(inputStream));
                                } catch (final IOException ex) {
                                    ex.printStackTrace();
                                    task.complete(null);
                                }
                            } else {
                                task.complete(null);
                            }
                        } else {
                            task.complete(null);
                        }

                        statement.close();
                        resultSet.close();
                    } catch (final SQLException ex) {
                        ex.printStackTrace();
                        task.complete(null);
                    }
                });
                return task;
            }

            @Nonnull
            @Override
            public Task<JsonConfiguration> findIfAbsent(@Nonnull String identifier) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    try {
                        PreparedStatement statement = connection.prepareStatement("SELECT `data` FROM `" + table + "` WHERE `identifier` = ?");
                        statement.setString(1, identifier);
                        ResultSet resultSet = statement.executeQuery();
                        if (resultSet.next()) {
                            byte[] bytes = resultSet.getBytes("data");
                            if (bytes.length != 0) {
                                try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                                    task.complete(new JsonConfiguration(inputStream));
                                } catch (final IOException ex) {
                                    ex.printStackTrace();
                                    task.complete(null);
                                }
                            } else {
                                task.complete(null);
                            }
                        } else {
                            task.complete(null);
                        }

                        statement.close();
                        resultSet.close();
                    } catch (final SQLException ex) {
                        ex.printStackTrace();
                        task.complete(null);
                    }
                });
                return task;
            }

            @Nonnull
            @Override
            public Task<JsonConfiguration> insert(@Nonnull String key, String identifier, @Nonnull JsonConfiguration data) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    try {
                        PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + table + "` (`key`, `identifier`, `data`) VALUES (?, ?, ?);");
                        statement.setString(1, key);
                        statement.setString(2, identifier);
                        statement.setBytes(3, data.toPrettyBytes());
                        statement.executeUpdate();
                        statement.close();
                        task.complete(data);
                    } catch (final SQLException ex) {
                        ex.printStackTrace();
                        task.complete(null);
                    }
                });
                return task;
            }

            @Nonnull
            @Override
            public Task<Boolean> update(@Nonnull String key, @Nonnull JsonConfiguration newData) {
                Task<Boolean> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    try {
                        PreparedStatement statement = connection.prepareStatement("UPDATE `" + table + "` SET `data` = ? WHERE `key` = ?");
                        statement.setBytes(1, newData.toPrettyBytes());
                        statement.setString(2, key);
                        statement.executeUpdate();
                        statement.close();
                        task.complete(true);
                    } catch (final SQLException ex) {
                        ex.printStackTrace();
                        task.complete(false);
                    }
                });
                return task;
            }

            @Nonnull
            @Override
            public Task<Boolean> updateIfAbsent(@Nonnull String identifier, @Nonnull JsonConfiguration newData) {
                Task<Boolean> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    JsonConfiguration configuration = findIfAbsent(identifier).getUninterruptedly();
                    if (configuration == null) {
                        task.complete(false);
                    } else {
                        removeIfAbsent(identifier);
                        try {
                            PreparedStatement statement = connection.prepareStatement("SELECT `key` FROM `" + table + "` WHERE `identifier` = ?");
                            statement.setString(1, identifier);
                            ResultSet resultSet = statement.executeQuery();
                            if (resultSet.next()) {
                                insert(resultSet.getString("key"), identifier, newData);
                                task.complete(true);
                            } else {
                                task.complete(false);
                            }
                        } catch (final SQLException ex) {
                            ex.printStackTrace();
                            task.complete(false);
                        }
                    }
                });
                return task;
            }

            @Nonnull
            @Override
            public Task<Void> remove(@Nonnull String key) {
                Task<Void> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    try {
                        PreparedStatement statement = connection.prepareStatement("DELETE FROM `" + table + "` WHERE `key` = ?");
                        statement.setString(1, key);
                        statement.executeUpdate();
                        statement.close();
                    } catch (final SQLException ex) {
                        ex.printStackTrace();
                    }

                    task.complete(null);
                });
                return task;
            }

            @Nonnull
            @Override
            public Task<Void> removeIfAbsent(@Nonnull String identifier) {
                Task<Void> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    try {
                        PreparedStatement statement = connection.prepareStatement("DELETE FROM `" + table + "` WHERE `identifier` = ?");
                        statement.setString(1, identifier);
                        statement.executeUpdate();
                        statement.close();
                    } catch (final SQLException ex) {
                        ex.printStackTrace();
                    }

                    task.complete(null);
                });
                return task;
            }

            @Nonnull
            @Override
            public Task<Boolean> contains(@Nonnull String key) {
                Task<Boolean> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> task.complete(find(key).getUninterruptedly() != null));
                return task;
            }

            @Nonnull
            @Override
            public Task<Integer> size() {
                Task<Integer> task = new DefaultTask<>();
                Task.EXECUTOR.execute(() -> {
                    try {
                        AtomicInteger atomicInteger = new AtomicInteger();
                        PreparedStatement statement = connection.prepareStatement("SELECT `data` FROM `" + table + "`");
                        ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            atomicInteger.addAndGet(1);
                        }
                        task.complete(atomicInteger.get());
                        statement.close();
                        resultSet.close();
                    } catch (final SQLException ex) {
                        ex.printStackTrace();
                        task.complete(0);
                    }
                });
                return task;
            }

            @Nonnull
            @Override
            public String getName() {
                return table;
            }

            @Override
            @Nonnull
            public Iterator<JsonConfiguration> iterator() {
                List<JsonConfiguration> list = new ArrayList<>();
                try {
                    AtomicInteger atomicInteger = new AtomicInteger();
                    PreparedStatement statement = connection.prepareStatement("SELECT `data` FROM `" + table + "`");
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        byte[] bytes = resultSet.getBytes("data");
                        if (bytes.length != 0) {
                            try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                                list.add(new JsonConfiguration(inputStream));
                            } catch (final IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    statement.close();
                    resultSet.close();
                } catch (final SQLException ex) {
                    ex.printStackTrace();
                }

                return list.iterator();
            }
        });
    }

    @Override
    public Connection get() {
        return connection;
    }

    private void startReconnect() {
        TaskScheduler.INSTANCE.schedule(() -> createDatabase("internal_users"), 0, 5, TimeUnit.MINUTES);
    }
}
