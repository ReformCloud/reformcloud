package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.rethinkdb;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.net.Connection;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.AbsentMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URL;
import java.util.Map;

public final class RethinkDBDatabase extends Database<RethinkDB> {

    private final Map<String, DatabaseReader> perTableReader = new AbsentMap<>();

    public RethinkDBDatabase() {
        URL dependency = DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
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
    public void connect(@Nonnull String host, int port, @Nonnull String userName, @Nonnull String password, @Nonnull String table) {
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
        //if (this.get().tableList().filter(table -> table.g(name)).run(connection).forEach(e -> System.out.println(e instanceof Table))) {
        //    return false;
        //}

        this.get().tableList().run(connection).forEach(e -> System.out.println(e instanceof Table));
        this.get().tableCreate(name).run(connection);
        return true;
    }

    @Override
    public boolean deleteDatabase(String name) {
        if (!this.get().tableList().filter(table -> table.g(name)).run(connection).hasNext()) {
            return false;
        }

        this.get().tableDrop(name).run(connection);
        return true;
    }

    @Nullable
    @Override
    public DatabaseReader createForTable(String table) {
        this.createDatabase(table);
        return this.perTableReader.putIfAbsent(table, new RethinkDatabaseDatabaseReader(this, this.connection, table));
    }

    @Nonnull
    @Override
    public RethinkDB get() {
        return RethinkDB.r;
    }
}
