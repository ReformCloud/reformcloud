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
package systems.reformcloud.reformcloud2.executor.api.common.database.config;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.database.Database;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class DatabaseConfig {

    private JsonConfiguration configuration;

    private DatabaseType type;

    public void load() {
        if (!Files.exists(Paths.get("reformcloud/configs/database.json"))) {
            new JsonConfiguration()
                    .add("type", DatabaseType.H2)
                    .add("host", "127.0.0.1")
                    .add("port", -1)
                    .add("user", "reformcloud")
                    .add("password", "reformcloud2222")
                    .add("table", "default")
                    .write(Paths.get("reformcloud/configs/database.json"));
        }

        this.configuration = JsonConfiguration.read(Paths.get("reformcloud/configs/database.json"));
        this.type = this.configuration.get("type", DatabaseType.class);
    }

    public void reload() {
        this.load();
    }

    public void connect(Database<?> database) {
        database.connect(
                this.configuration.getString("host"),
                this.configuration.getInteger("port"),
                this.configuration.getString("user"),
                this.configuration.getString("password"),
                this.configuration.getString("table")
        );
    }

    public DatabaseType getType() {
        return this.type;
    }

    public enum DatabaseType {

        FILE,

        H2,

        MONGO,

        MYSQL,

        RETHINK_DB
    }
}
