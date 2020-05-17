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
package systems.reformcloud.reformcloud2.executor.api.common.database.basic.drivers.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;
import systems.reformcloud.reformcloud2.executor.api.common.database.DatabaseReader;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.DefaultDependency;
import systems.reformcloud.reformcloud2.executor.api.common.dependency.repo.DefaultRepositories;
import systems.reformcloud.reformcloud2.executor.api.common.utility.StringUtil;
import systems.reformcloud.reformcloud2.executor.api.common.utility.maps.AbsentMap;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Map;

public final class MongoDatabase extends Database<com.mongodb.client.MongoDatabase> {

    private final Map<String, DatabaseReader> perTableReader = new AbsentMap<>();
    private MongoClient mongoClient;
    private com.mongodb.client.MongoDatabase mongoDatabase;

    public MongoDatabase() {
        URL dependency = DEPENDENCY_LOADER.loadDependency(new DefaultDependency(
                DefaultRepositories.MAVEN_CENTRAL,
                "org.mongodb",
                "mongo-java-driver",
                "3.12.4"
        ));
        Conditions.nonNull(dependency, StringUtil.formatError("dependency load for mongo database"));
        DEPENDENCY_LOADER.addDependency(dependency);
    }

    @Override
    public void connect(@NotNull String host, int port, @NotNull String userName, @NotNull String password, @NotNull String table) {
        if (!isConnected()) {
            try {
                this.mongoClient = MongoClients.create(
                        MessageFormat.format(
                                "mongodb://{0}:{1}@{2}:{3}/{4}",
                                userName,
                                URLEncoder.encode(password, StandardCharsets.UTF_8.name()),
                                host,
                                Integer.toString(port),
                                table
                        )
                );
                this.mongoDatabase = mongoClient.getDatabase(table);
            } catch (final UnsupportedEncodingException ex) {
                ex.printStackTrace(); //Should never happen
            }
        }
    }

    @Override
    public boolean isConnected() {
        return mongoClient != null;
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            this.mongoClient.close();
            this.mongoClient = null;
        }
    }

    @Override
    public boolean createDatabase(String name) {
        mongoDatabase.getCollection(name);
        return true;
    }

    @Override
    public boolean deleteDatabase(String name) {
        mongoDatabase.getCollection(name).drop();
        return true;
    }

    @Override
    public DatabaseReader createForTable(String table) {
        this.createDatabase(table);
        return perTableReader.putIfAbsent(table, new MongoDatabaseReader(table, this));
    }

    @NotNull
    @Override
    public com.mongodb.client.MongoDatabase get() {
        return mongoDatabase;
    }
}
