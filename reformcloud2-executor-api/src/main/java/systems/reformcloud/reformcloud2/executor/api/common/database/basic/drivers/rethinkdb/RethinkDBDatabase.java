package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.rethinkdb;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.AbsentMap;

import java.net.URL;
import java.util.Map;

public final class RethinkDBDatabase extends Database<RethinkDB> {

    private final Map<String, DatabaseReader> perTableReader = new AbsentMap<>();

    public RethinkDBDatabase() {
        URL dependency = DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "org.slf4j",
                "slf4j-api",
                "1.7.30"
        ));
        Conditions.nonNull(dependency, StringUtil.formatError("dependency load for rethink database"));
        DEPENDENCY_LOADER.addDependency(dependency);

        dependency = DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "com.rethinkdb",
                "rethinkdb-driver",
                "2.4.1"
        ));
        Conditions.nonNull(dependency, StringUtil.formatError("dependency load for rethink database"));
        DEPENDENCY_LOADER.addDependency(dependency);
    }

    private Connection connection;

    @Override
    public void connect(@NotNull String host, int port, @NotNull String userName, @NotNull String password, @NotNull String table) {
        this.connection = this.get().connection()
                .hostname(host)
                .port(port)
                .user(userName, password)
                .db(table)
                .connect();
    }

    @Override
    public boolean isConnected() {
        return this.connection != null && this.connection.isOpen();
    }

    @Override
    public void reconnect() {
        if (this.isConnected()) {
            this.connection.reconnect();
        }
    }

    @Override
    public void disconnect() {
        this.connection.close(true);
        this.connection = null;
    }

    @Override
    public boolean createDatabase(String name) {
        try {
            this.get().tableCreate(name).run(connection);
            return true;
        } catch (final Throwable error) {
            return false;
        }
    }

    @Override
    public boolean deleteDatabase(String name) {
        try {
            this.get().tableDrop(name).run(connection);
            return true;
        } catch (final Throwable error) {
            return false;
        }
    }

    @Nullable
    @Override
    public DatabaseReader createForTable(String table) {
        this.createDatabase(table);
        return this.perTableReader.putIfAbsent(table, new RethinkDatabaseDatabaseReader(this, this.connection, table));
    }

    @NotNull
    @Override
    public RethinkDB get() {
        return RethinkDB.r;
    }
}
