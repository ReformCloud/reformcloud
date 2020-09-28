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
package systems.reformcloud.reformcloud2.node.database;

import org.h2.Driver;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.io.IOUtils;
import systems.reformcloud.reformcloud2.node.database.sql.AbstractSQLDatabaseProvider;
import systems.reformcloud.reformcloud2.node.database.util.SQLFunction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class H2DatabaseProvider extends AbstractSQLDatabaseProvider {

    private static final Path DB_PATH = Paths.get(System.getProperty("systems.reformcloud.h2-db-path", "reformcloud/.database/h2/h2_db"));

    public H2DatabaseProvider() {
        if (Files.isDirectory(DB_PATH)) {
            IOUtils.createDirectory(DB_PATH);
        }

        try {
            Driver.load();
            this.connection = DriverManager.getConnection("jdbc:h2:" + DB_PATH.toAbsolutePath());
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    private final Connection connection;

    @Override
    public void executeUpdate(@NotNull String query, @NonNls Object... objects) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            int i = 1;
            for (Object object : objects) {
                if (object instanceof byte[]) {
                    preparedStatement.setBytes(i++, (byte[]) object);
                } else {
                    preparedStatement.setString(i++, object.toString());
                }
            }

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    @NotNull
    public <T> T executeQuery(@NotNull String query, SQLFunction<ResultSet, T> function, @NotNull T defaultValue, @NonNls Object... objects) {
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            int i = 1;
            for (Object object : objects) {
                if (object instanceof byte[]) {
                    preparedStatement.setBytes(i++, (byte[]) object);
                } else {
                    preparedStatement.setString(i++, object.toString());
                }
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return function.apply(resultSet);
            } catch (Throwable throwable) {
                return defaultValue;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return defaultValue;
    }
}
