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
package systems.reformcloud.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.wrappers.DatabaseTableWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public class MongoDatabaseTableWrapper implements DatabaseTableWrapper {

  private final MongoCollection<Document> collection;

  public MongoDatabaseTableWrapper(MongoDatabase database, String name) {
    this.collection = database.getCollection(name);
  }

  @Override
  public void insert(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration data) {
    this.update(key, id, data);
  }

  @Override
  public void update(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration newData) {
    Optional<JsonConfiguration> configuration = this.get(key, id);
    newData.add("_key", key).add("_identifier", id);
    if (configuration.isPresent()) {
      this.collection.updateOne(Filters.eq("_key", key), JsonConfiguration.DEFAULT_ADAPTER.fromJson(newData.toPrettyString(), Document.class));
    } else {
      this.collection.insertOne(JsonConfiguration.DEFAULT_ADAPTER.fromJson(newData.toPrettyString(), Document.class));
    }
  }

  @Override
  public void remove(@NotNull String key, @NotNull String id) {
    if (this.collection.deleteOne(Filters.eq("_key", key)).getDeletedCount() > 0) {
      return;
    }

    this.collection.deleteOne(Filters.eq("_identifier", id));
  }

  @Override
  public @NotNull Optional<JsonConfiguration> get(@NotNull String key, @NotNull String id) {
    Optional<JsonConfiguration> configuration = this.find("_key", key);
    return configuration.isPresent() ? configuration : this.find("_identifier", id);
  }

  @Override
  public @NotNull @UnmodifiableView Collection<String> getEntryNames() {
    Collection<String> collection = new ArrayList<>();
    this.collection.find().forEach((Consumer<Document>) e -> collection.add(e.getString("_key")));
    return collection;
  }

  @Override
  public long count() {
    return this.collection.countDocuments();
  }

  @Override
  public void clear() {
    this.collection.drop();
  }

  @Override
  public @NotNull @UnmodifiableView Collection<JsonConfiguration> getAll() {
    Collection<JsonConfiguration> collection = new ArrayList<>();
    this.collection.find().forEach((Consumer<Document>) e -> collection.add(JsonConfiguration.newJsonConfiguration(e.toJson())));
    return collection;
  }

  @Override
  public boolean has(@NotNull String key) {
    return this.find("_key", key).isPresent();
  }

  private Optional<JsonConfiguration> find(String keyName, String expected) {
    Document document = this.collection.find(Filters.eq(keyName, expected)).first();
    if (document == null) {
      return Optional.empty();
    } else {
      JsonConfiguration configuration = JsonConfiguration.newJsonConfiguration(document.toJson());
      configuration.remove("_key").remove("_identifier");
      return Optional.of(configuration);
    }
  }
}
