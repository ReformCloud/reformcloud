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
package systems.refomcloud.reformcloud2.embedded.process;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.builder.ProcessBuilder;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Version;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.provider.ProcessProvider;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessCount;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessCountByProcessGroup;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessCountResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessInformationByName;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessInformationByUniqueId;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessInformationObjects;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessInformationObjectsByMainGroup;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessInformationObjectsByProcessGroup;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessInformationObjectsByVersion;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessInformationObjectsResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessInformationResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessUniqueIds;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeGetProcessUniqueIdsResult;
import systems.reformcloud.reformcloud2.protocol.node.ApiToNodeUpdateProcessInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class DefaultEmbeddedProcessProvider implements ProcessProvider {

    @NotNull
    @Override
    public Optional<ProcessWrapper> getProcessByName(@NotNull String name) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetProcessInformationByName(name))
            .map(result -> {
                if (result instanceof ApiToNodeGetProcessInformationResult) {
                    return new DefaultEmbeddedProcessWrapper(((ApiToNodeGetProcessInformationResult) result).getProcessInformation());
                }

                return null;
            });
    }

    @NotNull
    @Override
    public Optional<ProcessWrapper> getProcessByUniqueId(@NotNull UUID uniqueId) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetProcessInformationByUniqueId(uniqueId))
            .map(result -> {
                if (result instanceof ApiToNodeGetProcessInformationResult) {
                    return new DefaultEmbeddedProcessWrapper(((ApiToNodeGetProcessInformationResult) result).getProcessInformation());
                }

                return null;
            });
    }

    @NotNull
    @Override
    public ProcessBuilder createProcess() {
        return new DefaultEmbeddedProcessBuilder();
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<ProcessInformation> getProcesses() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetProcessInformationObjects())
            .map(result -> {
                if (result instanceof ApiToNodeGetProcessInformationObjectsResult) {
                    return ((ApiToNodeGetProcessInformationObjectsResult) result).getProcessInformation();
                }

                return new ArrayList<ProcessInformation>();
            }).orElseGet(Collections::emptyList);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<ProcessInformation> getProcessesByProcessGroup(@NotNull String processGroup) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetProcessInformationObjectsByProcessGroup(processGroup))
            .map(result -> {
                if (result instanceof ApiToNodeGetProcessInformationObjectsResult) {
                    return ((ApiToNodeGetProcessInformationObjectsResult) result).getProcessInformation();
                }

                return new ArrayList<ProcessInformation>();
            }).orElseGet(Collections::emptyList);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<ProcessInformation> getProcessesByMainGroup(@NotNull String mainGroup) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetProcessInformationObjectsByMainGroup(mainGroup))
            .map(result -> {
                if (result instanceof ApiToNodeGetProcessInformationObjectsResult) {
                    return ((ApiToNodeGetProcessInformationObjectsResult) result).getProcessInformation();
                }

                return new ArrayList<ProcessInformation>();
            }).orElseGet(Collections::emptyList);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<ProcessInformation> getProcessesByVersion(@NotNull Version version) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetProcessInformationObjectsByVersion(version))
            .map(result -> {
                if (result instanceof ApiToNodeGetProcessInformationObjectsResult) {
                    return ((ApiToNodeGetProcessInformationObjectsResult) result).getProcessInformation();
                }

                return new ArrayList<ProcessInformation>();
            }).orElseGet(Collections::emptyList);
    }

    @NotNull
    @Override
    public @UnmodifiableView Collection<UUID> getProcessUniqueIds() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetProcessUniqueIds())
            .map(result -> {
                if (result instanceof ApiToNodeGetProcessUniqueIdsResult) {
                    return ((ApiToNodeGetProcessUniqueIdsResult) result).getUniqueIds();
                }

                return new ArrayList<UUID>();
            }).orElseGet(Collections::emptyList);
    }

    @Override
    public long getProcessCount() {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetProcessCount())
            .map(result -> {
                if (result instanceof ApiToNodeGetProcessCountResult) {
                    return ((ApiToNodeGetProcessCountResult) result).getResult();
                }

                return 0L;
            }).orElseGet(() -> 0L);
    }

    @Override
    public long getProcessCount(@NotNull String processGroup) {
        return Embedded.getInstance().sendSyncQuery(new ApiToNodeGetProcessCountByProcessGroup(processGroup))
            .map(result -> {
                if (result instanceof ApiToNodeGetProcessCountResult) {
                    return ((ApiToNodeGetProcessCountResult) result).getResult();
                }

                return 0L;
            }).orElseGet(() -> 0L);
    }

    @Override
    public void updateProcessInformation(@NotNull ProcessInformation processInformation) {
        Embedded.getInstance().sendPacket(new ApiToNodeUpdateProcessInformation(processInformation));
    }
}
