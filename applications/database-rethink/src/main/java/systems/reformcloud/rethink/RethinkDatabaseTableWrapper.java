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
package systems.reformcloud.rethink;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.gen.ast.Table;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Result;
import com.rethinkdb.utils.Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.wrappers.DatabaseTableWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class RethinkDatabaseTableWrapper implements DatabaseTableWrapper {

  private static final TypeReference<Map<String, String>> STRING_MAP_TYPE = Types.mapOf(String.class, String.class);
  private final Connection connection;
  private final Table table;

  public RethinkDatabaseTableWrapper(Connection connection, Db database, String targetTable) {
    this.connection = connection;
    this.table = database.table(targetTable);
  }

  @Override
  public void insert(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration data) {
    Optional<JsonConfiguration> jsonConfiguration = this.find(key, id);
    if (jsonConfiguration.isPresent()) {
      this.table.update(this.asMap(key, id, data)).runNoReply(this.connection);
    } else {
      this.table.insert(this.asMap(key, id, data)).runNoReply(this.connection);
    }
  }

  @Override
  public void update(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration newData) {
    this.insert(key, id, newData);
  }

  @Override
  public void remove(@NotNull String key, @NotNull String id) {
    this.table.filter(this.asMap(key, id)).delete().runNoReply(this.connection);
  }

  @Override
  public @NotNull Optional<JsonConfiguration> get(@NotNull String key, @NotNull String id) {
    return this.find(key, id);
  }

  @Override
  public @NotNull @UnmodifiableView Collection<String> getEntryNames() {
    Collection<String> result = new ArrayList<>();
    try (Result<Map<String, String>> run = this.table.run(this.connection, STRING_MAP_TYPE)) {
      while (run.hasNext()) {
        Map<String, String> map = run.next();
        if (map == null) {
          continue;
        }

        result.add(map.get("_key"));
      }
    }

    return result;
  }

  @Override
  public long count() {
    return this.getEntryNames().size(); // pail
  }

  @Override
  public void clear() {
    this.table.delete().runNoReply(this.connection);
  }

  @Override
  public @NotNull @UnmodifiableView Collection<JsonConfiguration> getAll() {
    Collection<JsonConfiguration> result = new ArrayList<>();
    try (Result<Map<String, String>> run = this.table.run(this.connection, STRING_MAP_TYPE)) {
      while (run.hasNext()) {
        Map<String, String> map = run.next();
        if (map == null) {
          continue;
        }

        result.add(JsonConfiguration.newJsonConfiguration(map.get("values")));
      }
    }

    return result;
  }

  @Override
  public boolean has(@NotNull String key) {
    return this.find(key, null).isPresent();
  }

  private Optional<JsonConfiguration> find(String key, @Nullable String id) {
    Result<Map<String, String>> result = this.table.filter(this.asMap(key, id)).run(this.connection, Types.mapOf(String.class, String.class));
    if (result.hasNext()) {
      Map<String, String> map = result.first();
      return map == null ? Optional.empty() : Optional.of(JsonConfiguration.newJsonConfiguration(map.get("values")));
    }

    return Optional.empty();
  }

  private MapObject<Object, Object> asMap(String key, @Nullable String id) {
    MapObject<Object, Object> result = RethinkDB.r.hashMap("_key", key);
    if (id != null) {
      result.with("_identifier", id);
    }

    return result;
  }

  private MapObject<Object, Object> asMap(String key, String id, JsonConfiguration data) {
    return this.asMap(key, id).with("values", data.toPrettyString());
  }
}
