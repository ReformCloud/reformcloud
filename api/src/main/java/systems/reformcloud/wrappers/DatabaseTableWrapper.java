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

public interface DatabaseTableWrapper {

  void insert(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration data);

  void update(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration newData);

  void remove(@NotNull String key, @NotNull String id);

  @NotNull
  Optional<JsonConfiguration> get(@NotNull String key, @NotNull String id);

  @NotNull
  @UnmodifiableView Collection<String> getEntryNames();

  long count();

  void clear();

  @NotNull
  @UnmodifiableView Collection<JsonConfiguration> getAll();

  boolean has(@NotNull String key);

  @NotNull
  default Task<Void> insertAsync(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration data) {
    return Task.supply(() -> {
      this.insert(key, id, data);
      return null;
    });
  }

  @NotNull
  default Task<Void> updateAsync(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration newData) {
    return Task.supply(() -> {
      this.update(key, id, newData);
      return null;
    });
  }

  @NotNull
  default Task<Void> removeAsync(@NotNull String key, @NotNull String id) {
    return Task.supply(() -> {
      this.remove(key, id);
      return null;
    });
  }

  @NotNull
  default Task<Optional<JsonConfiguration>> getAsync(@NotNull String key, @NotNull String id) {
    return Task.supply(() -> this.get(key, id));
  }

  @NotNull
  default Task<Collection<String>> getEntryNamesAsync() {
    return Task.supply(this::getEntryNames);
  }

  @NotNull
  default Task<Long> countAsync() {
    return Task.supply(this::count);
  }

  @NotNull
  default Task<Void> clearAsync() {
    return Task.supply(() -> {
      this.clear();
      return null;
    });
  }

  @NotNull
  default Task<Collection<JsonConfiguration>> getAllAsync() {
    return Task.supply(this::getAll);
  }

  @NotNull
  default Task<Boolean> hasAsync(@NotNull String key) {
    return Task.supply(() -> this.has(key));
  }
}
