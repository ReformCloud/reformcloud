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

import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.application.Application;
import systems.reformcloud.configuration.JsonConfiguration;
import systems.reformcloud.mongo.config.MongoConfig;
import systems.reformcloud.provider.DatabaseProvider;
import systems.reformcloud.shared.dependency.DependencyFileLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class MongoApplication extends Application {

  private DatabaseProvider before;

  @Override
  public void onLoad() {
    ExecutorAPI.getInstance().getDependencyLoader().load(
      DependencyFileLoader.collectDependenciesFromFile(MongoApplication.class.getClassLoader().getResourceAsStream("dependencies.txt"))
    );

    Path configPath = this.getDataDirectory().resolve("config.json");
    if (Files.notExists(configPath)) {
      JsonConfiguration.newJsonConfiguration()
        .add("config", new MongoConfig("127.0.0.1", 3306, "cloud", "cloud", ""))
        .write(configPath);
    }

    MongoConfig config = JsonConfiguration.newJsonConfiguration(configPath).get("config", MongoConfig.class);
    if (config == null) {
      System.err.println("Unable to load configuration for mongo module");
      return;
    }

    this.before = ExecutorAPI.getInstance().getDatabaseProvider();
    ExecutorAPI.getInstance().getServiceRegistry().setProvider(DatabaseProvider.class, new MongoDatabaseProvider(config), false, true);
  }

  @Override
  public void onDisable() {
    DatabaseProvider providerUnchecked = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(DatabaseProvider.class);
    if (providerUnchecked instanceof MongoDatabaseProvider) {
      ((MongoDatabaseProvider) providerUnchecked).close();
      ExecutorAPI.getInstance().getServiceRegistry().setProvider(DatabaseProvider.class, this.before, false, true);
    }
  }
}