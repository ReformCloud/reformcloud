/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.registry.io;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

/**
 * This class represents a registry with all keys in it
 */
public interface FileRegistry {

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
    @NotNull
    <T> Optional<T> getKey(@NotNull String keyName);

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
    @NotNull
    <T> Optional<T> updateKey(@NotNull String key, @NotNull T newValue);

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
