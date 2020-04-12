package systems.reformcloud.reformcloud2.executor.api.common.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependencyLoader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DependencyLoader;

/**
 * Represents an database of the cloud system
 *
 * @param <V> The type of the connection or types in it
 */
public abstract class Database<V> {

    protected static final DependencyLoader DEPENDENCY_LOADER = new DefaultDependencyLoader();

    /**
     * Connects to the database
     *
     * @param host     The host of the database
     * @param port     The port of the database
     * @param userName The user which should be used
     * @param password The password of the user
     * @param table    The table which the cloud system should use
     */
    public abstract void connect(
            @NotNull String host,
            int port,
            @NotNull String userName,
            @NotNull String password,
            @NotNull String table
    );

    /**
     * @return If the connection is open and writeable
     */
    public abstract boolean isConnected();

    /**
     * Reconnects to the database
     */
    public abstract void reconnect();

    /**
     * Disconnects from the database
     */
    public abstract void disconnect();

    /**
     * Creates a new database table
     *
     * @param name The name of the new table
     * @return If the table got created successfully
     */
    public abstract boolean createDatabase(String name);

    /**
     * Deletes a database
     *
     * @param name The name of the database
     * @return If the table got deleted successfully
     */
    public abstract boolean deleteDatabase(String name);

    /**
     * Creates a {@link DatabaseReader} for the table
     *
     * @param table The table for which the reader should be for
     * @return The creates {@link DatabaseReader} for the table
     */
    @Nullable
    public abstract DatabaseReader createForTable(String table);

    /**
     * @return The type parameter depending to the database type
     */
    @NotNull
    public abstract V get();
}
