package systems.reformcloud.reformcloud2.executor.api.common.registry;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
     * @param t The object which should get inserted in a json config
     * @param <T> The type of the object
     * @return The object which should get inserted or the value which is already inserted
     */
    @Nonnull
    <T> T createKey(@Nonnull String keyName, @Nonnull T t);

    /**
     * Gets a key from the registry
     *
     * @param keyName The name of the key which should get
     * @param <T> The type of the object
     * @return The key in the registry or {@code null}
     */
    @Nullable
    <T> T getKey(@Nonnull String keyName);

    /**
     * Deletes a key from the registry
     *
     * @param key The key which should get deleted
     */
    void deleteKey(@Nonnull String key);

    /**
     * Updates a key in the registry
     *
     * @param key The key which should get updates
     * @param newValue The value which should get updated
     * @param <T> The type of the new value
     * @return The value which should get updated
     */
    @Nullable
    <T> T updateKey(@Nonnull String key, @Nonnull T newValue);

    /**
     * Reads all keys from the registry
     *
     * @param function The function which should apply a json config to the object
     * @param <T> The type of the object
     * @return A collection of all objects in the database
     */
    @Nonnull
    <T> Collection<T> readKeys(@Nonnull Function<JsonConfiguration, T> function);
}
