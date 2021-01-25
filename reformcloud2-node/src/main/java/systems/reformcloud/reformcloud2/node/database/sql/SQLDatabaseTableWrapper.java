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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.reformcloud.reformcloud2.executor.api.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.wrappers.DatabaseTableWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public final class SQLDatabaseTableWrapper implements DatabaseTableWrapper {

    private final String name;
    private final AbstractSQLDatabaseProvider provider;

    protected SQLDatabaseTableWrapper(@NotNull String name, @NotNull AbstractSQLDatabaseProvider provider) {
        this.name = name;
        this.provider = provider;

        provider.executeUpdate("CREATE TABLE IF NOT EXISTS `" + name + "` (`key` TEXT, `identifier` TEXT, `data` LONGBLOB);");
    }

    @Override
    public void insert(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration data) {
        if (this.has(key)) {
            this.update(key, id, data);
        } else {
            this.provider.executeUpdate(
                "INSERT INTO `" + this.name + "` (`key`, `identifier`, `data`) VALUES (?, ?, ?);",
                key, id, data.toPrettyBytes()
            );
        }
    }

    @Override
    public void update(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration newData) {
        this.provider.executeUpdate(
            "UPDATE `" + this.name + "` SET `data` = ? WHERE `key` = ? AND (`identifier` = ? OR `identifier` IS NULL)",
            newData.toPrettyBytes(), key, id
        );
    }

    @Override
    public void remove(@NotNull String key, @NotNull String id) {
        this.provider.executeUpdate(
            "DELETE FROM `" + this.name + "` WHERE `key` = ?" + (id.isEmpty() ? "" : " AND (`identifier` = ? OR `identifier` IS NULL)"),
            key, id.isEmpty() ? null : id
        );
    }

    @NotNull
    @Override
    public Optional<JsonConfiguration> get(@NotNull String key, @NotNull String id) {
        return this.provider.executeQuery(
            "SELECT `data` FROM `" + this.name + "` WHERE `key` = ?" + (id.isEmpty() ? "" : " AND (`identifier` = ? OR `identifier` IS NULL)"),
            resultSet -> {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                byte[] bytes = resultSet.getBytes("data");
                if (bytes == null) {
                    return Optional.empty();
                }

                try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                    return Optional.of(JsonConfiguration.newJsonConfiguration(inputStream));
                } catch (final IOException ex) {
                    ex.printStackTrace();
                    return Optional.empty();
                }
            }, Optional.empty(), key, id.isEmpty() ? null : id
        );
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<String> getEntryNames() {
        return this.provider.executeQuery(
            "SELECT `key` FROM " + this.name,
            resultSet -> {
                Collection<String> result = new ArrayList<>();
                while (resultSet.next()) {
                    result.add(resultSet.getString("key"));
                }

                return result;
            }, new ArrayList<>()
        );
    }

    @Override
    public long count() {
        return this.provider.executeQuery(
            "SELECT COUNT(*) FROM " + this.name,
            resultSet -> {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }

                return -1L;
            }, -1L);
    }

    @Override
    public void clear() {
        this.provider.executeUpdate("TRUNCATE TABLE " + this.name);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<JsonConfiguration> getAll() {
        return this.provider.executeQuery(
            "SELECT `data` FROM " + this.name,
            resultSet -> {
                Collection<JsonConfiguration> result = new ArrayList<>();
                while (resultSet.next()) {
                    byte[] bytes = resultSet.getBytes("data");
                    if (bytes == null) {
                        continue;
                    }

                    try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
                        result.add(JsonConfiguration.newJsonConfiguration(inputStream));
                    } catch (final IOException ex) {
                        ex.printStackTrace();
                    }
                }

                return result;
            }, new ArrayList<>()
        );
    }

    @Override
    public boolean has(@NotNull String key) {
        return this.provider.executeQuery(
            "SELECT `key` FROM " + this.name + " WHERE `key` = ?",
            resultSet -> resultSet.next() && resultSet.getString("key") != null,
            false,
            key
        );
    }
}
