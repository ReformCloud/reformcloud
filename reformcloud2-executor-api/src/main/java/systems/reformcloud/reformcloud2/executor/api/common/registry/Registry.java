package systems.reformcloud.reformcloud2.executor.api.common.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import java.util.Collection;
import java.util.function.Function;

/**
 * This class represents a registry with all keys in it
 */
public interface Registry {

    /**
     * Creates a new key in the registry
     *
     * @param keyName The name of the key
     * @param t       The object which should get inserted in a json config
     * @param <T>     The type of the object
     * @return The object which should get inserted or the value which is already inserted
     */
    @NotNull
    <T> T createKey(@NotNull String keyName, @NotNull T t);

    /**
     * Gets a key from the registry
     *
     * @param keyName The name of the key which should get
     * @param <T>     The type of the object
     * @return The key in the registry or {@code null}
     */
    @Nullable
    <T> T getKey(@NotNull String keyName);

    /**
     * Deletes a key from the registry
     *
     * @param key The key which should get deleted
     */
    void deleteKey(@NotNull String key);

    /**
     * Updates a key in the registry
     *
     * @param key      The key which should get updates
     * @param newValue The value which should get updated
     * @param <T>      The type of the new value
     * @return The value which should get updated
     */
    @Nullable
    <T> T updateKey(@NotNull String key, @NotNull T newValue);

    /**
     * Reads all keys from the registry
     *
     * @param function The function which should apply a json config to the object
     * @param <T>      The type of the object
     * @return A collection of all objects in the database
     */
    @NotNull
    <T> Collection<T> readKeys(@NotNull Function<JsonConfiguration, T> function);
}
