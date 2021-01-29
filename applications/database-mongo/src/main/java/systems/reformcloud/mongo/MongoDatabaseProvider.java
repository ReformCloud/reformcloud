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

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.provider.DatabaseProvider;
import systems.reformcloud.wrappers.DatabaseTableWrapper;
import systems.reformcloud.mongo.config.MongoConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

public class MongoDatabaseProvider implements DatabaseProvider, AutoCloseable {

  private final MongoClient client;
  private final MongoDatabase database;

  public MongoDatabaseProvider(MongoConfig config) {
    try {
      this.client = MongoClients.create(MessageFormat.format(
        "mongodb://{0}:{1}@{2}:{3}/{4}",
        config.getUserName(),
        URLEncoder.encode(config.getPassword(), StandardCharsets.UTF_8.name()),
        config.getHost(),
        Integer.toString(config.getPort()),
        config.getDatabase()
      ));
      this.database = this.client.getDatabase(config.getDatabase());
    } catch (UnsupportedEncodingException exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public @NotNull DatabaseTableWrapper createTable(@NotNull String tableName) {
    return new MongoDatabaseTableWrapper(this.database, tableName);
  }

  @Override
  public void deleteTable(@NotNull String tableName) {
    this.database.getCollection(tableName).drop();
  }

  @Override
  public @NotNull @UnmodifiableView Collection<String> getTableNames() {
    Collection<String> result = new ArrayList<>();
    for (String s : this.client.listDatabaseNames()) {
      result.add(s);
    }

    return result;
  }

  @Override
  public @NotNull DatabaseTableWrapper getDatabase(@NotNull String tableName) {
    return new MongoDatabaseTableWrapper(this.database, tableName);
  }

  @Override
  public void close() {
    this.client.close();
  }
}
