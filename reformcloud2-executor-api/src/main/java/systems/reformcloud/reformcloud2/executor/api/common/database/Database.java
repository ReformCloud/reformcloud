package systems.reformcloud.reformcloud2.executor.api.common.database;

import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;

public abstract class Database<V> {

    protected static final DependencyLoader DEPENDENCY_LOADER = new DefaultDependencyLoader();

    public abstract void connect(String host, int port, String userName, String password, String table);

    public abstract boolean isConnected();

    public abstract void reconnect();

    public abstract void disconnect();

    public abstract boolean createDatabase(String name);

    public abstract boolean deleteDatabase(String name);

    public abstract DatabaseReader createForTable(String table);

    public abstract V get();
}
