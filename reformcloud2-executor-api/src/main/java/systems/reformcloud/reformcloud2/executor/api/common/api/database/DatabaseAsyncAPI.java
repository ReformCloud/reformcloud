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
package systems.reformcloud.reformcloud2.executor.api.common.api.database;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.util.Collection;
import java.util.function.Function;

public interface DatabaseAsyncAPI {

    /**
     * Tries to find an object in the database
     *
     * @param table      The table name in which should be searched
     * @param key        The key of the entry
     * @param identifier The id if the key couldn't be found
     * @return A task which will be completed with a json config or {@code null} if the entry does not exists
     */
    @NotNull
    Task<JsonConfiguration> findAsync(@NotNull String table, @NotNull String key, @Nullable String identifier);

    /**
     * Tries to find an object in the database
     *
     * @param table      The table name in which should be searched
     * @param key        The key of the entry
     * @param identifier The id if the key couldn't be found
     * @param function   Tries to apply the json config to, to get the final needed object
     * @param <T>        The type which should be get out of the json config
     * @return A task which will be completed with the object or {@code null}
     */
    @NotNull
    <T> Task<T> findAsync(@NotNull String table, @NotNull String key, @Nullable String identifier, @NotNull Function<JsonConfiguration, T> function);

    /**
     * Gets all entries from a specific database
     *
     * @param table The table name from which all entries are needed
     * @return A collection of all documents in the given database table
     */
    @NotNull
    Task<Collection<JsonConfiguration>> getCompleteDatabaseAsync(@NotNull String table);

    /**
     * Gets all entries from a specific database applied to the given mapping function
     *
     * @param table  The table name in which should be searched
     * @param mapper Tries to apply the json config to, to get the final needed object
     * @param <T>    The type which should be get out of the json config
     * @return A collection of all documents in the given database table applied to the function and filtered for not-null
     */
    @NotNull
    <T> Task<Collection<T>> getCompleteDatabaseAsync(@NotNull String table, @NotNull Function<JsonConfiguration, T> mapper);

    /**
     * Inserts a json config into the database
     *
     * @param table      The table in which the cloud should insert the document
     * @param key        The key of the entry
     * @param identifier The identifier of the entry
     * @param data       The json config which should be inserted
     * @return A task which will be completed if the action was successful
     */
    @NotNull
    Task<Void> insertAsync(@NotNull String table, @NotNull String key, @Nullable String identifier, @NotNull JsonConfiguration data);

    /**
     * Updates a json config in the database
     *
     * @param table   The table in which the document is
     * @param key     The key of the document
     * @param newData The new value of the entry
     * @return A task which will be completed with {@code true} if the config got updated or {@code false}
     * @deprecated Use {@link #updateAsync(String, String, String, JsonConfiguration)} instead
     */
    @NotNull
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    Task<Boolean> updateAsync(@NotNull String table, @NotNull String key, @NotNull JsonConfiguration newData);

    /**
     * Updates a json config in the database
     *
     * @param table      The table in which the document is
     * @param identifier The identifier of the json config if the key is unknown
     * @param newData    The new value of the entry
     * @return A task which will be completed with {@code true} if the config got updated or {@code false}
     * @deprecated Use {@link #updateAsync(String, String, String, JsonConfiguration)} instead
     */
    @NotNull
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    Task<Boolean> updateIfAbsentAsync(@NotNull String table, @NotNull String identifier, @NotNull JsonConfiguration newData);

    /**
     * Updates a json config in the database
     *
     * @param table      The table in which the document is
     * @param key        The key of the document
     * @param identifier The identifier of the json config if the key is unknown
     * @param newData    The new value of the entry
     * @return A task completed after the request to update the document in the database
     */
    @NotNull
    Task<Void> updateAsync(@NotNull String table, @Nullable String key, @Nullable String identifier, @NotNull JsonConfiguration newData);

    /**
     * Removes an json config out of the database
     *
     * @param table The table in which the config is
     * @param key   The key of the config
     * @return A task which will be completed if the action is completed
     * @deprecated Use {@link #removeAsync(String, String, String)} instead
     */
    @NotNull
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    Task<Void> removeAsync(@NotNull String table, @NotNull String key);

    /**
     * Removes an json config out of the database
     *
     * @param table      The table in which the config is
     * @param identifier The id of the config if the key is unknown
     * @return A task which will be completed if the action is completed
     * @deprecated Use {@link #removeAsync(String, String, String)} instead
     */
    @NotNull
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    Task<Void> removeIfAbsentAsync(@NotNull String table, @NotNull String identifier);

    /**
     * Removes an json config out of the database
     *
     * @param table      The table in which the config is
     * @param key        The key of the config
     * @param identifier The id of the config if the key is unknown
     * @return A task completed after the request of delete of the document
     */
    @NotNull
    Task<Void> removeAsync(@NotNull String table, @Nullable String key, @Nullable String identifier);

    /**
     * Creates a new database with the given name
     *
     * @param name The name of the new database
     * @return A task which will be completed with {@code true} if the operation was successful else {@code false}
     */
    @NotNull
    Task<Boolean> createDatabaseAsync(@NotNull String name);

    /**
     * Deletes an database an all contents in it
     *
     * @param name The name of the database
     * @return A task which will be completed with {@code true} if the operation was successful else {@code false}
     */
    @NotNull
    Task<Boolean> deleteDatabaseAsync(@NotNull String name);

    /**
     * Checks if an specific key is in the database
     *
     * @param table The name of the table where the cloud should search in
     * @param key   The key of the entry
     * @return A task which will be completed with {@code true} if the value is present else {@code false}
     */
    @NotNull
    Task<Boolean> containsAsync(@NotNull String table, @NotNull String key);

    /**
     * Get the size of a table
     *
     * @param table The name of the table
     * @return A task which will be completed with the current size of the database
     */
    @NotNull
    Task<Integer> sizeAsync(@NotNull String table);
}
