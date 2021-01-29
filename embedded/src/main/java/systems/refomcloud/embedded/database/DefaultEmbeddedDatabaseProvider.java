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
package systems.refomcloud.embedded.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.refomcloud.embedded.Embedded;
import systems.reformcloud.provider.DatabaseProvider;
import systems.reformcloud.wrappers.DatabaseTableWrapper;
import systems.reformcloud.protocol.node.ApiToNodeDeleteDatabaseTable;
import systems.reformcloud.protocol.node.ApiToNodeGetDatabaseNames;
import systems.reformcloud.protocol.node.ApiToNodeGetDatabaseNamesResult;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultEmbeddedDatabaseProvider implements DatabaseProvider {

  @NotNull
  @Override
  public DatabaseTableWrapper createTable(@NotNull String tableName) {
    return new DefaultEmbeddedDatabaseTableWrapper(tableName);
  }

  @Override
  public void deleteTable(@NotNull String tableName) {
    Embedded.getInstance().sendPacket(new ApiToNodeDeleteDatabaseTable(tableName));
  }

  @NotNull
  @Override
  public @UnmodifiableView Collection<String> getTableNames() {
    return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetDatabaseNames())
      .map(result -> {
        if (result instanceof ApiToNodeGetDatabaseNamesResult) {
          return ((ApiToNodeGetDatabaseNamesResult) result).getNames();
        }

        return new ArrayList<String>();
      }).orElseGet(ArrayList::new);
  }

  @NotNull
  @Override
  public DatabaseTableWrapper getDatabase(@NotNull String tableName) {
    return new DefaultEmbeddedDatabaseTableWrapper(tableName);
  }
}
