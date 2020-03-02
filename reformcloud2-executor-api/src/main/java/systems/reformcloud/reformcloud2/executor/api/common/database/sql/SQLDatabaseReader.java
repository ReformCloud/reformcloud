package systems.reformcloud.reformcloud2.executor.api.common.database.sql;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class SQLDatabaseReader implements DatabaseReader {

    public SQLDatabaseReader(String table, Database<Connection> database) {
        this.table = table;
        this.database = database;
    }

    private final String table;

    private final Database<Connection> database;

    @Nonnull
    @Override
    public Task<JsonConfiguration> find(@Nonnull String key) {
        return this.get("key", key);
    }

    @Nonnull
    @Override
    public Task<JsonConfiguration> findIfAbsent(@Nonnull String identifier) {
        return this.get("identifier", identifier);
    }

    @Nonnull
    @Override
    public Task<JsonConfiguration> insert(@Nonnull String key, @Nullable String identifier, @Nonnull JsonConfiguration data) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Boolean has = this.contains(key).getUninterruptedly(TimeUnit.SECONDS, 5);
            if (has != null && has) {
                this.update(key, data).awaitUninterruptedly();
                task.complete(data);
                return;
            }

            try (PreparedStatement statement = prepareStatement("INSERT INTO `" + table + "` (`key`, `identifier`, `data`) VALUES (?, ?, ?);", database)) {
                statement.setString(1, key);
                statement.setString(2, identifier);
                statement.setBytes(3, data.toPrettyBytes());
                statement.executeUpdate();
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
            try (PreparedStatement statement = prepareStatement("UPDATE `" + table + "` SET `data` = ? WHERE `key` = ?", database)) {
                statement.setBytes(1, newData.toPrettyBytes());
                statement.setString(2, key);
                statement.executeUpdate();
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
            try (PreparedStatement statement = prepareStatement("UPDATE `" + table + "` SET `data` = ? WHERE `identifier` = ?", database)) {
                statement.setBytes(1, newData.toPrettyBytes());
                statement.setString(2, identifier);
                statement.executeUpdate();
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
    public Task<Void> remove(@Nonnull String key) {
       Task<Void> task = new DefaultTask<>();
       Task.EXECUTOR.execute(() -> {
           try (PreparedStatement statement = prepareStatement("DELETE FROM `" + table + "` WHERE `key` = ?", database)) {
               statement.setString(1, key);
               statement.executeUpdate();
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
            try (PreparedStatement statement = prepareStatement("DELETE FROM `" + table + "` WHERE `identifier` = ?", database)) {
                statement.setString(1, identifier);
                statement.executeUpdate();
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
        Task.EXECUTOR.execute(() -> task.complete(this.find(key).getUninterruptedly() != null));
        return task;
    }

    @Nonnull
    @Override
    public Task<Integer> size() {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            int count = 0;
            for (JsonConfiguration ignored : this) {
                count++;
            }

            task.complete(count);
        });
        return task;
    }

    @Nonnull
    @Override
    public Iterator<JsonConfiguration> iterator() {
        Collection<JsonConfiguration> list = new ArrayList<>();
        try (PreparedStatement statement = prepareStatement("SELECT `data` FROM `" + table + "`", database)) {
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
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return list.iterator();
    }

    @Nonnull
    @Override
    public String getName() {
        return this.table;
    }

    private Task<JsonConfiguration> get(String keyName, String key) {
        Task<JsonConfiguration> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            try (PreparedStatement statement = prepareStatement("SELECT `data` FROM `" + table + "` WHERE `" + keyName + "` = ?", database)) {
                statement.setString(1, key);
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next()) {
                    task.complete(null);
                    return;
                }

                byte[] bytes = resultSet.getBytes("data");
                if (bytes == null || bytes.length == 0) {
                    task.complete(null);
                    return;
                }

                try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                    task.complete(new JsonConfiguration(inputStream));
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            } catch (final SQLException ex) {
                ex.printStackTrace();
            }

            task.complete(null);
        });
        return task;
    }

    @Nonnull
    public static PreparedStatement prepareStatement(@Nonnull String sql, @Nonnull Database<Connection> database) throws SQLException {
        return database.get().prepareStatement(sql);
    }
}
