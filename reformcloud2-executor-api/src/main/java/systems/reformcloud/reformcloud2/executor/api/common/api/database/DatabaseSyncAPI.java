package systems.reformcloud.reformcloud2.executor.api.common.api.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import java.util.function.Function;

public interface DatabaseSyncAPI {

    /**
     * Tries to find an object in the database
     *
     * @param table      The table name in which should be searched
     * @param key        The key of the entry
     * @param identifier The id if the key couldn't be found
     * @return The json config or {@code null} if the entry does not exists
     */
    @Nullable
    JsonConfiguration find(@NotNull String table, @NotNull String key, @Nullable String identifier);

    /**
     * Tries to find an object in the database
     *
     * @param table      The table name in which should be searched
     * @param key        The key of the entry
     * @param identifier The id if the key couldn't be found
     * @param function   Tries to apply the json config to, to get the final needed object
     * @param <T>        The type which should be get out of the json config
     * @return The object or {@code null}
     */
    @Nullable
    <T> T find(
            @NotNull String table,
            @NotNull String key,
            @Nullable String identifier,
            @NotNull Function<JsonConfiguration, T> function
    );

    /**
     * Inserts a json config into the database
     *
     * @param table      The table in which the cloud should insert the document
     * @param key        The key of the entry
     * @param identifier The identifier of the entry
     * @param data       The json config which should be inserted
     */
    void insert(@NotNull String table, @NotNull String key, @Nullable String identifier, @NotNull JsonConfiguration data);

    /**
     * Updates a json config in the database
     *
     * @param table   The table in which the document is
     * @param key     The key of the document
     * @param newData The new value of the entry
     * @return {@code true} if the config got updated or {@code false}
     */
    boolean update(@NotNull String table, @NotNull String key, @NotNull JsonConfiguration newData);

    /**
     * Updates a json config in the database
     *
     * @param table      The table in which the document is
     * @param identifier The identifier of the json config if the key is unknown
     * @param newData    The new value of the entry
     * @return {@code true} if the config got updated or {@code false}
     */
    boolean updateIfAbsent(@NotNull String table, @NotNull String identifier, @NotNull JsonConfiguration newData);

    /**
     * Removes an json config out of the database
     *
     * @param table The table in which the config is
     * @param key   The key of the config
     */
    void remove(@NotNull String table, @NotNull String key);

    /**
     * Removes an json config out of the database
     *
     * @param table      The table in which the config is
     * @param identifier The id of the config if the key is unknown
     */
    void removeIfAbsent(@NotNull String table, @NotNull String identifier);

    /**
     * Creates a new database with the given name
     *
     * @param name The name of the new database
     * @return {@code true} if the operation was successful else {@code false}
     */
    boolean createDatabase(@NotNull String name);

    /**
     * Deletes an database an all contents in it
     *
     * @param name The name of the database
     * @return {@code true} if the operation was successful else {@code false}
     */
    boolean deleteDatabase(@NotNull String name);

    /**
     * Checks if an specific key is in the database
     *
     * @param table The name of the table where the cloud should search in
     * @param key   The key of the entry
     * @return {@code true} if the value is present else {@code false}
     */
    boolean contains(@NotNull String table, @NotNull String key);

    /**
     * Get the size of a table
     *
     * @param table The name of the table
     * @return The current size of the database
     */
    int size(@NotNull String table);
}
