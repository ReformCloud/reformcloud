package de.klaro.reformcloud2.executor.api.common.database;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.utility.name.Nameable;
import de.klaro.reformcloud2.executor.api.common.utility.task.Task;

/**
 * This class represents a database table where all operations are async in it
 */
public interface DatabaseReader extends Iterable<JsonConfiguration>, Nameable {

    Task<JsonConfiguration> find(String key);

    Task<JsonConfiguration> findIfAbsent(String identifier);

    Task<JsonConfiguration> insert(String key, String identifier, JsonConfiguration data);

    Task<Boolean> update(String key, JsonConfiguration newData);

    Task<Boolean> updateIfAbsent(String identifier, JsonConfiguration newData);

    Task<Void> remove(String key);

    Task<Void> removeIfAbsent(String identifier);

    Task<Boolean> contains(String key);

    Task<Integer> size();
}
