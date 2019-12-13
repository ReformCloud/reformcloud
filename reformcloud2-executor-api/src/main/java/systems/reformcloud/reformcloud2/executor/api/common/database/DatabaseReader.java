package systems.reformcloud.reformcloud2.executor.api.common.database;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an database with all values in it
 *
 * @see Database#createForTable(String)
 */
public interface DatabaseReader extends Iterable<JsonConfiguration>, Nameable {

    /**
     * Tries to find a json document in the database
     *
     * @param key The key which the cloud should search
     * @return A task which will be completed with the {@link JsonConfiguration} or {@code null} if the database does not contains the key
     */
    @Nonnull
    @CheckReturnValue
    Task<JsonConfiguration> find(@Nonnull String key);

    /**
     * Tries to find a json document in the database
     *
     * @param identifier The id which the cloud should search
     * @return A task which will be completed with the {@link JsonConfiguration} or {@code null} if the database does not contains the id
     */
    @Nonnull
    @CheckReturnValue
    Task<JsonConfiguration> findIfAbsent(@Nonnull String identifier);

    /**
     * Inserts a json document into the database
     *
     * @param key The key of the {@link JsonConfiguration}
     * @param identifier The id of the {@link JsonConfiguration}
     * @param data The {@link JsonConfiguration} which should be inserted
     * @return The {@link JsonConfiguration} after the insert of the document
     */
    @Nonnull
    @CheckReturnValue
    Task<JsonConfiguration> insert(@Nonnull String key, @Nullable String identifier, @Nonnull JsonConfiguration data);

    /**
     * Updates a document in the database
     *
     * @param key The key of the document
     * @param newData The new document which should be inserted
     * @return A task which will be completed with {@code true} if the operation was successful else {@code false}
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> update(@Nonnull String key, @Nonnull JsonConfiguration newData);

    /**
     * Updates a document in the database
     *
     * @param identifier The id of the document
     * @param newData The new document which should be inserted
     * @return A task which will be completed with {@code true} if the operation was successful else {@code false}
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> updateIfAbsent(@Nonnull String identifier, @Nonnull JsonConfiguration newData);

    /**
     * Removes a document out of the database
     *
     * @param key The key of the document which should be deleted
     * @return A task which will be completed after the execute of the operation
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> remove(@Nonnull String key);

    /**
     * Removes a document out of the database
     *
     * @param identifier The id of the document which should be deleted
     * @return A task which will be completed after the execute of the operation
     */
    @Nonnull
    @CheckReturnValue
    Task<Void> removeIfAbsent(@Nonnull String identifier);

    /**
     * Checks if the database contains the given key
     *
     * @param key They key which should be checked for
     * @return A task which will be completed with {@code true} if the database contains the document else {@code false}
     */
    @Nonnull
    @CheckReturnValue
    Task<Boolean> contains(@Nonnull String key);

    /**
     * @return A task which will be completed with the size of the current database
     */
    @Nonnull
    @CheckReturnValue
    Task<Integer> size();
}
