package systems.reformcloud.reformcloud2.executor.api.common.api.database;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public interface DatabaseAsyncAPI {

    /**
     * Tries to find an object in the database
     *
     * @param table The table name in which should be searched
     * @param key The key of the entry
     * @param identifier The id if the key couldn't be found
     * @return A task which will be completed with a json config or {@code null} if the entry does not exists
     */
    @Nonnull
    @CheckReturnValue
    Task<JsonConfiguration> findAsync(@Nonnull String table, @Nonnull String key, @Nullable String identifier);

    /**
     * Tries to find an object in the database
     *
     * @param table The table name in which should be searched
     * @param key The key of the entry
     * @param identifier The id if the key couldn't be found
     * @param function Tries to apply the json config to, to get the final needed object
     * @param <T> The type which should be get out of the json config
     * @return A task which will be completed with the object or {@code null}
     */
    @Nonnull
    @CheckReturnValue
    <T> Task<T> findAsync(
            @Nonnull String table,
            @Nonnull String key,
            @Nullable String identifier,
            @Nonnull Function<JsonConfiguration, T> function
    );

    /**
     * Inserts a json config into the database
     *
     * @param table The table in which the cloud should insert the document
     * @param key The key of the entry
     * @param identifier The identifier of the entry
     * @param data The json config which should be inserted
     * @return A task which will be completed if the action was successful
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> insertAsync(
            @Nonnull String table,
            @Nonnull String key,
            @Nullable String identifier,
            @Nonnull JsonConfiguration data
    );

    /**
     * Updates a json config in the database
     *
     * @param table The table in which the document is
     * @param key The key of the document
     * @param newData The new value of the entry
     * @return A task which will be completed with {@code true} if the config got updated or {@code false}
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> updateAsync(@Nonnull String table, @Nonnull String key, @Nonnull JsonConfiguration newData);

    /**
     * Updates a json config in the database
     *
     * @param table The table in which the document is
     * @param identifier The identifier of the json config if the key is unknown
     * @param newData The new value of the entry
     * @return A task which will be completed with {@code true} if the config got updated or {@code false}
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> updateIfAbsentAsync(@Nonnull String table, @Nonnull String identifier, @Nonnull JsonConfiguration newData);

    /**
     * Removes an json config out of the database
     *
     * @param table The table in which the config is
     * @param key The key of the config
     * @return A task which will be completed if the action is completed
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> removeAsync(@Nonnull String table, @Nonnull String key);

    /**
     * Removes an json config out of the database
     *
     * @param table The table in which the config is
     * @param identifier The id of the config if the key is unknown
     * @return A task which will be completed if the action is completed
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> removeIfAbsentAsync(@Nonnull String table, @Nonnull String identifier);

    /**
     * Creates a new database with the given name
     *
     * @param name The name of the new database
     * @return A task which will be completed with {@code true} if the operation was successful else {@code false}
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> createDatabaseAsync(@Nonnull String name);

    /**
     * Deletes an database an all contents in it
     *
     * @param name The name of the database
     * @return A task which will be completed with {@code true} if the operation was successful else {@code false}
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> deleteDatabaseAsync(@Nonnull String name);

    /**
     * Checks if an specific key is in the database
     *
     * @param table The name of the table where the cloud should search in
     * @param key The key of the entry
     * @return A task which will be completed with {@code true} if the value is present else {@code false}
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> containsAsync(@Nonnull String table, @Nonnull String key);

    /**
     * Get the size of a table
     *
     * @param table The name of the table
     * @return A task which will be completed with the current size of the database
     */
    @Nonnull
    @CheckReturnValue
    Task<Integer> sizeAsync(@Nonnull String table);
}
