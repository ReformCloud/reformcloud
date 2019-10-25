package systems.reformcloud.reformcloud2.executor.api.common.database;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

/**
 * This class represents a database table where all operations are async in it
 */
public interface DatabaseReader extends Iterable<JsonConfiguration>, Nameable {

    /**
     * Tries to find a json document in the database
     *
     * @param key The key which the cloud should search
     * @return A task which will be completed with the {@link JsonConfiguration} or {@code null} if the database does not contains the key
     */
    Task<JsonConfiguration> find(String key);

    /**
     * Tries to find a json document in the database
     *
     * @param identifier The id which the cloud should search
     * @return A task which will be completed with the {@link JsonConfiguration} or {@code null} if the database does not contains the id
     */
    Task<JsonConfiguration> findIfAbsent(String identifier);

    /**
     * Inserts a json document into the database
     *
     * @param key The key of the {@link JsonConfiguration}
     * @param identifier The id of the {@link JsonConfiguration}
     * @param data The {@link JsonConfiguration} which should be inserted
     * @return The {@link JsonConfiguration} after the insert of the document
     */
    Task<JsonConfiguration> insert(String key, String identifier, JsonConfiguration data);

    /**
     * Updates a document in the database
     *
     * @param key The key of the document
     * @param newData The new document which should be inserted
     * @return A task which will be completed with {@code true} if the operation was successful else {@code false}
     */
    Task<Boolean> update(String key, JsonConfiguration newData);

    /**
     * Updates a document in the database
     *
     * @param identifier The id of the document
     * @param newData The new document which should be inserted
     * @return A task which will be completed with {@code true} if the operation was successful else {@code false}
     */
    Task<Boolean> updateIfAbsent(String identifier, JsonConfiguration newData);

    /**
     * Removes a document out of the database
     *
     * @param key The key of the document which should be deleted
     * @return A task which will be completed after the execute of the operation
     */
    Task<Void> remove(String key);

    /**
     * Removes a document out of the database
     *
     * @param identifier The id of the document which should be deleted
     * @return A task which will be completed after the execute of the operation
     */
    Task<Void> removeIfAbsent(String identifier);

    /**
     * Checks if the database contains the given key
     *
     * @param key They key which should be checked for
     * @return A task which will be completed with {@code true} if the database contains the document else {@code false}
     */
    Task<Boolean> contains(String key);

    /**
     * @return A task which will be completed with the size of the current database
     */
    Task<Integer> size();
}
