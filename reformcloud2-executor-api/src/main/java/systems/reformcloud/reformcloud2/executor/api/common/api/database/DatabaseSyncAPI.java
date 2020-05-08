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
     * @deprecated Use {@link #update(String, String, String, JsonConfiguration)} instead
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    boolean update(@NotNull String table, @NotNull String key, @NotNull JsonConfiguration newData);

    /**
     * Updates a json config in the database
     *
     * @param table      The table in which the document is
     * @param identifier The identifier of the json config if the key is unknown
     * @param newData    The new value of the entry
     * @return {@code true} if the config got updated or {@code false}
     * @deprecated Use {@link #update(String, String, String, JsonConfiguration)} instead
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    boolean updateIfAbsent(@NotNull String table, @NotNull String identifier, @NotNull JsonConfiguration newData);

    /**
     * Updates a json config in the database
     *
     * @param table      The table in which the document is
     * @param key        The key of the document
     * @param identifier The identifier of the json config if the key is unknown
     * @param newData    The new value of the entry
     */
    void update(@NotNull String table, @Nullable String key, @Nullable String identifier, @NotNull JsonConfiguration newData);

    /**
     * Removes an json config out of the database
     *
     * @param table The table in which the config is
     * @param key   The key of the config
     * @deprecated Use {@link #remove(String, String, String)} instead
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    void remove(@NotNull String table, @NotNull String key);

    /**
     * Removes an json config out of the database
     *
     * @param table      The table in which the config is
     * @param identifier The id of the config if the key is unknown
     * @deprecated Use {@link #remove(String, String, String)} instead
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    void removeIfAbsent(@NotNull String table, @NotNull String identifier);

    /**
     * Removes an json config out of the database
     *
     * @param table      The table in which the config is
     * @param key        The key of the config
     * @param identifier The id of the config if the key is unknown
     */
    void remove(@NotNull String table, @Nullable String key, @Nullable String identifier);

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
