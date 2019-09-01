package de.klaro.reformcloud2.executor.api.common.api.database;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.utility.annotiations.Nullable;

import java.util.function.Function;

public interface DatabaseSyncAPI {

    JsonConfiguration find(String table, String key, @Nullable String identifier);

    <T> T find(String table, String key, Function<JsonConfiguration, T> function);

    void insert(String table, String key, @Nullable String identifier, JsonConfiguration data);

    void remove(String table, String key);

    boolean createDatabase(String name);

    boolean deleteDatabase(String name);
}
