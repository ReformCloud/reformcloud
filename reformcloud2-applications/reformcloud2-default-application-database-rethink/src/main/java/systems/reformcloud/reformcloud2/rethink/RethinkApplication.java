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
package systems.reformcloud.reformcloud2.rethink;

import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.application.api.Application;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.provider.DatabaseProvider;
import systems.reformcloud.reformcloud2.rethink.config.RethinkConfig;
import systems.reformcloud.reformcloud2.shared.dependency.DependencyFileLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class RethinkApplication extends Application {

    private DatabaseProvider before;

    @Override
    public void onLoad() {
        ExecutorAPI.getInstance().getDependencyLoader().load(
            DependencyFileLoader.collectDependenciesFromFile(RethinkApplication.class.getClassLoader().getResourceAsStream("dependencies.txt"))
        );

        this.before = ExecutorAPI.getInstance().getDatabaseProvider();
        Path configPath = this.getDataFolder().toPath().resolve("config.json");
        if (Files.notExists(configPath)) {
            new JsonConfiguration().add("config", new RethinkConfig("127.0.0.1", 3306, "cloud", "cloud", null)).write(configPath);
        }

        RethinkConfig config = JsonConfiguration.read(configPath).get("config", RethinkConfig.class);
        if (config == null) {
            System.err.println("Unable to load configuration for rethink module");
            return;
        }

        ExecutorAPI.getInstance().getServiceRegistry().setProvider(DatabaseProvider.class, new RethinkDatabaseProvider(config), false, true);
    }

    @Override
    public void onDisable() {
        DatabaseProvider providerUnchecked = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(DatabaseProvider.class);
        if (providerUnchecked instanceof RethinkDatabaseProvider) {
            ((RethinkDatabaseProvider) providerUnchecked).close();
            ExecutorAPI.getInstance().getServiceRegistry().setProvider(DatabaseProvider.class, this.before, false, true);
        }
    }
}
