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
package systems.reformcloud.reformcloud2.executor.api.common.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.name.Nameable;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;

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
    @NotNull
    Task<JsonConfiguration> find(@NotNull String key);

    /**
     * Tries to find a json document in the database
     *
     * @param identifier The id which the cloud should search
     * @return A task which will be completed with the {@link JsonConfiguration} or {@code null} if the database does not contains the id
     */
    @NotNull
    Task<JsonConfiguration> findIfAbsent(@NotNull String identifier);

    /**
     * Inserts a json document into the database
     *
     * @param key        The key of the {@link JsonConfiguration}
     * @param identifier The id of the {@link JsonConfiguration}
     * @param data       The {@link JsonConfiguration} which should be inserted
     * @return The {@link JsonConfiguration} after the insert of the document
     */
    @NotNull
    Task<JsonConfiguration> insert(@NotNull String key, @Nullable String identifier, @NotNull JsonConfiguration data);

    /**
     * Updates a document in the database
     *
     * @param key     The key of the document
     * @param newData The new document which should be inserted
     * @return A task which will be completed with {@code true} if the operation was successful else {@code false}
     */
    @NotNull
    Task<Boolean> update(@NotNull String key, @NotNull JsonConfiguration newData);

    /**
     * Updates a document in the database
     *
     * @param identifier The id of the document
     * @param newData    The new document which should be inserted
     * @return A task which will be completed with {@code true} if the operation was successful else {@code false}
     */
    @NotNull
    Task<Boolean> updateIfAbsent(@NotNull String identifier, @NotNull JsonConfiguration newData);

    /**
     * Removes a document out of the database
     *
     * @param key The key of the document which should be deleted
     * @return A task which will be completed after the execute of the operation
     */
    @NotNull
    Task<Void> remove(@NotNull String key);

    /**
     * Removes a document out of the database
     *
     * @param identifier The id of the document which should be deleted
     * @return A task which will be completed after the execute of the operation
     */
    @NotNull
    Task<Void> removeIfAbsent(@NotNull String identifier);

    /**
     * Checks if the database contains the given key
     *
     * @param key They key which should be checked for
     * @return A task which will be completed with {@code true} if the database contains the document else {@code false}
     */
    @NotNull
    Task<Boolean> contains(@NotNull String key);

    /**
     * @return A task which will be completed with the size of the current database
     */
    @NotNull
    default Task<Integer> size() {
        Task<Integer> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            int count = 0;
            for (JsonConfiguration ignored : this) {
                count++;
            }
            task.complete(count);
        });
        return task;
    }
}
