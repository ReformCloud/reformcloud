package de.klaro.reformcloud2.executor.api.common.database.basic.drivers.mysql;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.database.Database;
import de.klaro.reformcloud2.executor.api.common.database.DatabaseReader;
import de.klaro.reformcloud2.executor.api.common.dependency.DefaultDependency;
import de.klaro.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import de.klaro.reformcloud2.executor.api.common.utility.task.Task;
import de.klaro.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class MySQLDatabase extends Database<Connection> {

    private final Map<String, DatabaseReader> perTableReader = new ConcurrentHashMap<>();

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
            @Override
            public Task<JsonConfiguration> find(String key) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
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
                    }
                });
                return task;
            }

            @Override
            public Task<JsonConfiguration> findIfAbsent(String identifier) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
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
                    }
                });
                return task;
            }

            @Override
            public Task<JsonConfiguration> insert(String key, String identifier, JsonConfiguration data) {
                Task<JsonConfiguration> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
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
                    }
                });
                return task;
            }

            @Override
            public Task<Void> remove(String key, String identifier) {
                Task<Void> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PreparedStatement statement = connection.prepareStatement("DELETE FROM `" + table + "` WHERE `key` = ?");
                            statement.setString(1, key);
                            statement.executeUpdate();
                            statement.close();
                        } catch (final SQLException ex) {
                            ex.printStackTrace();
                        }

                        task.complete(null);
                    }
                });
                return task;
            }

            @Override
            public Task<Void> removeIfAbsent(String identifier) {
                Task<Void> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PreparedStatement statement = connection.prepareStatement("DELETE FROM `" + table + "` WHERE `identifier` = ?");
                            statement.setString(1, identifier);
                            statement.executeUpdate();
                            statement.close();
                        } catch (final SQLException ex) {
                            ex.printStackTrace();
                        }

                        task.complete(null);
                    }
                });
                return task;
            }

            @Override
            public Task<Boolean> contains(String key) {
                Task<Boolean> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        task.complete(find(key).getUninterruptedly() != null);
                    }
                });
                return task;
            }

            @Override
            public Task<Integer> size() {
                Task<Integer> task = new DefaultTask<>();
                Task.EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
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
                    }
                });
                return task;
            }

            @Override
            public String getName() {
                return table;
            }

            @Override
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
}
