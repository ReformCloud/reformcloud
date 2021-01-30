/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.wrappers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.task.Task;

import java.util.Collection;
import java.util.Optional;

/**
 * A wrapper for a database table.
 */
public interface DatabaseTableWrapper {

  /**
   * Inserts a value into the database table.
   *
   * @param key  the key of the entry.
   * @param id   the identifier of the entry.
   * @param data the data of the entry.
   */
  void insert(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration data);

  /**
   * Updates a value into the database table.
   *
   * @param key     the key of the entry.
   * @param id      the identifier of the entry.
   * @param newData the new data of the entry.
   */
  void update(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration newData);

  /**
   * Removes an entry from the table.
   *
   * @param key The key of the entry.
   * @param id  The id of the entry.
   */
  void remove(@NotNull String key, @NotNull String id);

  /**
   * Gets an entry from this table.
   *
   * @param key The key of the entry.
   * @param id  The id of the entry.
   * @return The entry.
   */
  @NotNull
  Optional<JsonConfiguration> get(@NotNull String key, @NotNull String id);

  /**
   * Gets all entry names from the database.
   *
   * @return All entry names from the database.
   */
  @NotNull
  @UnmodifiableView Collection<String> getEntryNames();

  /**
   * Gets the count of objects in the database table.
   *
   * @return The count of objects in the database table.
   */
  long count();

  /**
   * Clears this table.
   */
  void clear();

  /**
   * Get all values in the database.
   *
   * @return All values in the database.
   */
  @NotNull
  @UnmodifiableView Collection<JsonConfiguration> getAll();

  /**
   * Checks if the table contains the specified key.
   *
   * @param key The key to check.
   * @return {@code true} if the table contains the key, else {@code false}.
   */
  boolean has(@NotNull String key);

  /**
   * Inserts a value into the database table asynchronously.
   *
   * @param key  the key of the entry.
   * @param id   the identifier of the entry.
   * @param data the data of the entry.
   * @return A future completed when the operation was completed.
   */
  @NotNull
  default Task<Void> insertAsync(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration data) {
    return Task.supply(() -> {
      this.insert(key, id, data);
      return null;
    });
  }

  /**
   * Updates a value into the database table asynchronously.
   *
   * @param key     the key of the entry.
   * @param id      the identifier of the entry.
   * @param newData the new data of the entry.
   * @return A future completed when the operation was completed.
   */
  @NotNull
  default Task<Void> updateAsync(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration newData) {
    return Task.supply(() -> {
      this.update(key, id, newData);
      return null;
    });
  }

  /**
   * Removes an entry from the table asynchronously.
   *
   * @param key The key of the entry.
   * @param id  The id of the entry.
   * @return A future completed when the operation was completed.
   */
  @NotNull
  default Task<Void> removeAsync(@NotNull String key, @NotNull String id) {
    return Task.supply(() -> {
      this.remove(key, id);
      return null;
    });
  }

  /**
   * Gets an entry from this table asynchronously.
   *
   * @param key The key of the entry.
   * @param id  The id of the entry.
   * @return A future completed with the entry.
   */
  @NotNull
  default Task<Optional<JsonConfiguration>> getAsync(@NotNull String key, @NotNull String id) {
    return Task.supply(() -> this.get(key, id));
  }

  /**
   * Gets all entry names from the database asynchronously.
   *
   * @return A future completed with all entry names from the database.
   */
  @NotNull
  default Task<Collection<String>> getEntryNamesAsync() {
    return Task.supply(this::getEntryNames);
  }

  /**
   * Gets the count of objects in the database table asynchronously.
   *
   * @return A task completed with the count of objects in the database table.
   */
  @NotNull
  default Task<Long> countAsync() {
    return Task.supply(this::count);
  }

  /**
   * Clears this table asynchronously.
   *
   * @return A future completed when the operation was completed.
   */
  @NotNull
  default Task<Void> clearAsync() {
    return Task.supply(() -> {
      this.clear();
      return null;
    });
  }

  /**
   * Get all values in the database asynchronously.
   *
   * @return A future completed with all values in the database.
   */
  @NotNull
  default Task<Collection<JsonConfiguration>> getAllAsync() {
    return Task.supply(this::getAll);
  }

  /**
   * Checks if the table contains the specified key asynchronously.
   *
   * @param key The key to check.
   * @return A future completed with {@code true} if the table contains the key, else {@code false}.
   */
  @NotNull
  default Task<Boolean> hasAsync(@NotNull String key) {
    return Task.supply(() -> this.has(key));
  }
}
