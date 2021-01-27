/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.reformcloud.reformcloud2.executor.api.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.wrappers.DatabaseTableWrapper;

import java.util.Collection;

/**
 * Provides the accessibility methods to the {@link DatabaseTableWrapper} and for managing the internal
 * currently used database either internal provided (by default H2) or externally provided by an
 * application (for example MySQL). This interface will always execute in the same way and is not
 * database dependant.
 *
 * @author Pasqual Koschmieder
 * @since 2.10.0
 */
public interface DatabaseProvider {

  /**
   * Creates a new table in the currently used database if not exists already.
   * <p>Note that you should always cache these table wrappers instead of always getting them newly
   * because the changes in the table are not object dependant</p>
   *
   * @param tableName The name of the table which should get created
   * @return The newly created database table of the already existing one
   */
  @NotNull
  DatabaseTableWrapper createTable(@NotNull String tableName);

  /**
   * Deletes a specific database table if it exists
   *
   * @param tableName The name of the table which should get deleted
   */
  void deleteTable(@NotNull String tableName);

  /**
   * Gets all exiting database names from the internal database which is used for the system.
   *
   * @return All table names in the current operating database
   */
  @NotNull
  @UnmodifiableView Collection<String> getTableNames();

  /**
   * Gets an existing database which does not have to exist so it may be empty an will always result
   * in empty queries.Updates may create the table or have no effect, too.
   * <p>Note that you should always cache these table wrappers instead of always getting them newly
   * because the changes in the table are not object dependant</p>
   *
   * @param tableName The name of the table
   * @return A wrapper for the database table by the given name
   */
  @NotNull
  DatabaseTableWrapper getDatabase(@NotNull String tableName);

  /**
   * This method does the same as {@link #createTable(String)} but asynchronously.
   *
   * @param tableName The name of the table which should get created
   * @return The newly created database table of the already existing one
   */
  @NotNull
  default Task<DatabaseTableWrapper> createTableAsync(@NotNull String tableName) {
    return Task.supply(() -> this.createTable(tableName));
  }

  /**
   * This method does the same as {@link #deleteTable(String)} but asynchronously.
   *
   * @param tableName The name of the table which should get deleted
   * @return A task completed after the delete of the database table or directly if there is no need to block the operation
   */
  @NotNull
  default Task<Void> deleteTableAsync(@NotNull String tableName) {
    return Task.supply(() -> {
      this.deleteTable(tableName);
      return null;
    });
  }

  /**
   * This method does the same as {@link #getTableNames()} but asynchronously.
   *
   * @return All table names in the current operating database
   */
  @NotNull
  default Task<Collection<String>> getTableNamesAsync() {
    return Task.supply(this::getTableNames);
  }

  /**
   * This method does the same as {@link #getDatabase(String)} but asynchronously.
   *
   * @param tableName The name of the table
   * @return A wrapper for the database table by the given name
   */
  @NotNull
  default Task<DatabaseTableWrapper> getDatabaseAsync(@NotNull String tableName) {
    return Task.supply(() -> this.getDatabase(tableName));
  }
}
