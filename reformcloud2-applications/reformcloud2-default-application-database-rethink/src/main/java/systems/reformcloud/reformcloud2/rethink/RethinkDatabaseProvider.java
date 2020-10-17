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

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Db;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.provider.DatabaseProvider;
import systems.reformcloud.reformcloud2.executor.api.wrappers.DatabaseTableWrapper;
import systems.reformcloud.reformcloud2.rethink.config.RethinkConfig;

import java.util.Collection;

public class RethinkDatabaseProvider implements DatabaseProvider, AutoCloseable {

    private final Connection connection;
    private final Db database;

    public RethinkDatabaseProvider(RethinkConfig config) {
        this.connection = RethinkDB.r.connection()
            .hostname(config.getHost())
            .port(config.getPort())
            .user(config.getUserName(), config.getPassword())
            .db(config.getDatabase())
            .connect();
        this.database = RethinkDB.r.db(config.getDatabase());
    }

    @Override
    public @NotNull DatabaseTableWrapper createTable(@NotNull String tableName) {
        return new RethinkDatabaseTableWrapper(this.connection, this.database, tableName);
    }

    @Override
    public void deleteTable(@NotNull String tableName) {
        this.database.tableDrop(tableName).run(this.connection);
    }

    @Override
    public @NotNull @UnmodifiableView Collection<String> getTableNames() {
        try (Result<String> result = this.database.tableList().run(this.connection, String.class)) {
            return result.toList();
        }
    }

    @Override
    public @NotNull DatabaseTableWrapper getDatabase(@NotNull String tableName) {
        return new RethinkDatabaseTableWrapper(this.connection, this.database, tableName);
    }

    @Override
    public void close() {
        this.connection.close();
    }
}
