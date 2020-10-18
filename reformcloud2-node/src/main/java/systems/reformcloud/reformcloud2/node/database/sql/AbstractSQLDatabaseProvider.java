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
package systems.reformcloud.reformcloud2.node.database.sql;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.provider.DatabaseProvider;
import systems.reformcloud.reformcloud2.executor.api.wrappers.DatabaseTableWrapper;
import systems.reformcloud.reformcloud2.node.database.util.SQLFunction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSQLDatabaseProvider implements DatabaseProvider {

    protected final Map<String, DatabaseTableWrapper> wrapperCache = new ConcurrentHashMap<>();

    @NotNull
    @Override
    public DatabaseTableWrapper createTable(@NotNull String tableName) {
        return this.getDatabase(tableName);
    }

    @Override
    public void deleteTable(@NotNull String tableName) {
        this.executeUpdate("DROP TABLE " + tableName);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<String> getTableNames() {
        return this.executeQuery(
            "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='PUBLIC'",
            resultSet -> {
                Collection<String> collection = new ArrayList<>();
                while (resultSet.next()) {
                    collection.add(resultSet.getString("table_name"));
                }

                return collection;
            }, new ArrayList<>()
        );
    }

    @NotNull
    @Override
    public DatabaseTableWrapper getDatabase(@NotNull String tableName) {
        DatabaseTableWrapper wrapper = this.wrapperCache.get(tableName);
        if (wrapper != null) {
            return wrapper;
        }

        wrapper = new SQLDatabaseTableWrapper(tableName, this);
        this.wrapperCache.put(tableName, wrapper);
        return wrapper;
    }

    public abstract void executeUpdate(@NotNull String query, @NonNls Object... objects);

    @NotNull
    public abstract <T> T executeQuery(@NotNull String query, @NotNull SQLFunction<ResultSet, T> function, @NotNull T defaultValue, @NonNls Object... objects);

    protected void appendObjectsToPreparedStatement(@NotNull PreparedStatement statement, @NonNls Object... objects) throws SQLException {
        int i = 1;
        for (Object object : objects) {
            if (object instanceof byte[]) {
                statement.setBytes(i++, (byte[]) object);
            } else {
                statement.setString(i++, object.toString());
            }
        }
    }
}
