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
package systems.refomcloud.reformcloud2.embedded.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.wrappers.DatabaseTableWrapper;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeClearDatabaseTable;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetAllTableEntries;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetAllTableEntriesResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetDatabaseDocument;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetDatabaseDocumentResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetDatabaseEntryCount;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetDatabaseEntryCountResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetTableEntryNames;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetTableEntryNamesResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeHasTableDocument;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeHasTableDocumentResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeInsertDocumentIntoTable;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeRemoveDocumentFromTable;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeUpdateDocumentInTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class DefaultEmbeddedDatabaseTableWrapper implements DatabaseTableWrapper {

    private final String tableName;

    public DefaultEmbeddedDatabaseTableWrapper(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void insert(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration data) {
        Embedded.getInstance().sendPacket(new ApiToNodeInsertDocumentIntoTable(this.tableName, key, id, data));
    }

    @Override
    public void update(@NotNull String key, @NotNull String id, @NotNull JsonConfiguration newData) {
        Embedded.getInstance().sendPacket(new ApiToNodeUpdateDocumentInTable(this.tableName, key, id, newData));
    }

    @Override
    public void remove(@NotNull String key, @NotNull String id) {
        Embedded.getInstance().sendPacket(new ApiToNodeRemoveDocumentFromTable(this.tableName, key, id));
    }

    @NotNull
    @Override
    public Optional<JsonConfiguration> get(@NotNull String key, @NotNull String id) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetDatabaseDocument(this.tableName, key, id))
            .map(result -> {
                if (result instanceof ApiToNodeGetDatabaseDocumentResult) {
                    return ((ApiToNodeGetDatabaseDocumentResult) result).getResult();
                }

                return new JsonConfiguration();
            });
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<String> getEntryNames() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetTableEntryNames(this.tableName))
            .map(result -> {
                if (result instanceof ApiToNodeGetTableEntryNamesResult) {
                    return ((ApiToNodeGetTableEntryNamesResult) result).getNames();
                }

                return new ArrayList<String>();
            }).orElseGet(ArrayList::new);
    }

    @Override
    public long count() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetDatabaseEntryCount(this.tableName))
            .map(result -> {
                if (result instanceof ApiToNodeGetDatabaseEntryCountResult) {
                    return ((ApiToNodeGetDatabaseEntryCountResult) result).getCount();
                }

                return 0L;
            }).orElseGet(() -> 0L);
    }

    @Override
    public void clear() {
        Embedded.getInstance().sendPacket(new ApiToNodeClearDatabaseTable(this.tableName));
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<JsonConfiguration> getAll() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetAllTableEntries(this.tableName))
            .map(result -> {
                if (result instanceof ApiToNodeGetAllTableEntriesResult) {
                    return ((ApiToNodeGetAllTableEntriesResult) result).getAll();
                }

                return new ArrayList<JsonConfiguration>();
            }).orElseGet(ArrayList::new);
    }

    @Override
    public boolean has(@NotNull String key) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeHasTableDocument(this.tableName, key))
            .map(result -> {
                if (result instanceof ApiToNodeHasTableDocumentResult) {
                    return ((ApiToNodeHasTableDocumentResult) result).has();
                }

                return false;
            }).orElseGet(() -> false);
    }
}
