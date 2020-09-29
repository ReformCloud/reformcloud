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
package systems.reformcloud.reformcloud2.node.cluster;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.node.NodeInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.wrappers.ProcessWrapper;

import java.util.Collection;
import java.util.UUID;

public interface ClusterManager {

    @NotNull
    Task<ProcessWrapper> createProcess(@NotNull ProcessGroup processGroup, @Nullable String node, @Nullable String displayName,
                                       @Nullable String messageOfTheDay, @Nullable Template template, @NotNull Collection<ProcessInclusion> inclusions,
                                       @NotNull JsonConfiguration jsonConfiguration, @NotNull ProcessState initialState,
                                       @NotNull UUID uniqueId, int memory, int id, int maxPlayers, @Nullable String targetProcessFactory);

    void handleNodeConnect(@NotNull NodeInformation nodeInformation);

    void handleNodeUpdate(@NotNull NodeInformation nodeInformation);

    void publishNodeUpdate(@NotNull NodeInformation nodeInformation);

    void handleNodeDisconnect(@NotNull String name);

    void handleProcessRegister(@NotNull ProcessInformation processInformation);

    void publishProcessRegister(@NotNull ProcessInformation processInformation);

    void handleProcessUpdate(@NotNull ProcessInformation processInformation);

    void publishProcessUpdate(@NotNull ProcessInformation processInformation);

    void handleProcessUnregister(@NotNull String name);

    void publishProcessUnregister(@NotNull ProcessInformation processInformation);

    void handleProcessSet(@NotNull Collection<ProcessInformation> processInformation);

    void publishProcessSet(@NotNull Collection<ProcessInformation> processInformation);

    void handleProcessGroupCreate(@NotNull ProcessGroup processGroup);

    void publishProcessGroupCreate(@NotNull ProcessGroup processGroup);

    void handleProcessGroupUpdate(@NotNull ProcessGroup processGroup);

    void publishProcessGroupUpdate(@NotNull ProcessGroup processGroup);

    void handleProcessGroupDelete(@NotNull ProcessGroup processGroup);

    void publishProcessGroupDelete(@NotNull ProcessGroup processGroup);

    void handleProcessGroupSet(@NotNull Collection<ProcessGroup> processGroups);

    void publishProcessGroupSet(@NotNull Collection<ProcessGroup> processGroups);

    void handleMainGroupCreate(@NotNull MainGroup mainGroup);

    void publishMainGroupCreate(@NotNull MainGroup mainGroup);

    void handleMainGroupUpdate(@NotNull MainGroup mainGroup);

    void publishMainGroupUpdate(@NotNull MainGroup mainGroup);

    void handleMainGroupDelete(@NotNull MainGroup mainGroup);

    void publishMainGroupDelete(@NotNull MainGroup mainGroup);

    void handleMainGroupSet(@NotNull Collection<MainGroup> mainGroups);

    void publishMainGroupSet(@NotNull Collection<MainGroup> mainGroups);

    boolean isHeadNode();

    @NotNull NodeInformation getHeadNode();
}
