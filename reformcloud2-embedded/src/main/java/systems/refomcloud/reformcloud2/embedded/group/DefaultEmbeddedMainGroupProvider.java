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
package systems.refomcloud.reformcloud2.embedded.group;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.builder.MainGroupBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.main.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.provider.MainGroupProvider;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeDeleteMainGroup;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetMainGroup;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetMainGroupCount;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetMainGroupCountResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetMainGroupNames;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetMainGroupObjects;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetMainGroupObjectsResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetMainGroupResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetStringCollectionResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeUpdateMainGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class DefaultEmbeddedMainGroupProvider implements MainGroupProvider {

    @NotNull
    @Override
    public Optional<MainGroup> getMainGroup(@NotNull String name) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetMainGroup(name))
            .map(result -> {
                if (result instanceof ApiToNodeGetMainGroupResult) {
                    return ((ApiToNodeGetMainGroupResult) result).getMainGroup();
                }

                return null;
            });
    }

    @Override
    public void deleteMainGroup(@NotNull String name) {
        Embedded.getInstance().sendPacket(new ApiToNodeDeleteMainGroup(name));
    }

    @Override
    public void updateMainGroup(@NotNull MainGroup mainGroup) {
        Embedded.getInstance().sendPacket(new ApiToNodeUpdateMainGroup(mainGroup));
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<MainGroup> getMainGroups() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetMainGroupObjects())
            .map(result -> {
                if (result instanceof ApiToNodeGetMainGroupObjectsResult) {
                    return ((ApiToNodeGetMainGroupObjectsResult) result).getMainGroups();
                }

                return new ArrayList<MainGroup>();
            }).orElseGet(Collections::emptyList);
    }

    @Override
    public long getMainGroupCount() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetMainGroupCount())
            .map(result -> {
                if (result instanceof ApiToNodeGetMainGroupCountResult) {
                    return ((ApiToNodeGetMainGroupCountResult) result).getCount();
                }

                return 0L;
            }).orElseGet(() -> 0L);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<String> getMainGroupNames() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetMainGroupNames())
            .map(result -> {
                if (result instanceof ApiToNodeGetStringCollectionResult) {
                    return ((ApiToNodeGetStringCollectionResult) result).getResult();
                }

                return new ArrayList<String>();
            }).orElseGet(Collections::emptyList);
    }

    @NotNull
    @Override
    public MainGroupBuilder createMainGroup(@NotNull String name) {
        return new DefaultEmbeddedMainGroupBuilder().name(name);
    }
}
