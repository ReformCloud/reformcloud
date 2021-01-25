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
package systems.reformcloud.reformcloud2.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.mysql.config.MySQLDatabaseConfig;
import systems.reformcloud.reformcloud2.node.database.sql.AbstractSQLDatabaseProvider;
import systems.reformcloud.reformcloud2.node.database.util.SQLFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLDatabaseProvider extends AbstractSQLDatabaseProvider {

  private static final String CONNECT_ARGUMENTS = "jdbc:mysql://%s:%d/%s?serverTimezone=UTC";
  private final HikariDataSource hikariDataSource;

  public MySQLDatabaseProvider(@NotNull MySQLDatabaseConfig config) {
    HikariConfig hikariConfig = new HikariConfig();

    hikariConfig.setJdbcUrl(String.format(CONNECT_ARGUMENTS, config.getHost(), config.getPort(), config.getDatabase()));
    hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
    hikariConfig.setUsername(config.getUserName());
    hikariConfig.setPassword(config.getPassword());

    hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
    hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
    hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
    hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
    hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
    hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
    hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
    hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
    hikariConfig.addDataSourceProperty("maintainTimeStats", "false");

    this.hikariDataSource = new HikariDataSource(hikariConfig);
  }

  public void close() {
    this.hikariDataSource.close();
  }

  @Override
  public void executeUpdate(@NotNull String query, @NonNls Object... objects) {
    try (Connection connection = this.hikariDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      this.appendObjectsToPreparedStatement(preparedStatement, objects);
      preparedStatement.executeUpdate();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }

  @Override
  @NotNull
  public <T> T executeQuery(@NotNull String query, @NotNull SQLFunction<ResultSet, T> function, @NotNull T defaultValue, @NonNls Object... objects) {
    try (Connection connection = this.hikariDataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      this.appendObjectsToPreparedStatement(preparedStatement, objects);
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
