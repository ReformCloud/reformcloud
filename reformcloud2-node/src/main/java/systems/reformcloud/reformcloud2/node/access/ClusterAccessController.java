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
package systems.reformcloud.reformcloud2.node.access;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.configuration.gson.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.packet.query.QueryManager;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.process.api.ProcessInclusion;
import systems.reformcloud.reformcloud2.executor.api.task.Task;
import systems.reformcloud.reformcloud2.executor.api.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.node.cluster.ClusterManager;
import systems.reformcloud.reformcloud2.node.factory.ProcessFactoryConfiguration;
import systems.reformcloud.reformcloud2.node.factory.ProcessFactoryController;
import systems.reformcloud.reformcloud2.node.protocol.NodeToHeadNodeCreateProcess;
import systems.reformcloud.reformcloud2.node.protocol.NodeToHeadNodeCreateProcessResult;

import java.util.Collection;
import java.util.UUID;

public final class ClusterAccessController {

    private ClusterAccessController() {
        throw new AssertionError("You should not instantiate this class");
    }

    public static void ensurePrivileged(@NotNull String reason) {
        if (!getClusterManager().isHeadNode()) {
            throw new IllegalStateException("Unprivileged " + reason + "!");
        }
    }

    @NotNull
    public static Task<ProcessInformation> createProcessPrivileged(@NotNull ProcessGroup processGroup, @Nullable String node,
                                                                   @Nullable String displayName, @Nullable String messageOfTheDay,
                                                                   @Nullable Template template, @NotNull Collection<ProcessInclusion> inclusions,
                                                                   @NotNull JsonConfiguration jsonConfiguration, @NotNull ProcessState initial, @NotNull UUID uniqueId,
                                                                   int memory, int id, int maxPlayers, @Nullable String targetProcessFactory) {
        return createProcessPrivileged(
                new ProcessFactoryConfiguration(node, displayName, messageOfTheDay, processGroup, template, inclusions, jsonConfiguration, initial, uniqueId, memory, id, maxPlayers),
                targetProcessFactory
        );
    }

    @NotNull
    public static Task<ProcessInformation> createProcessPrivileged(@NotNull ProcessFactoryConfiguration configuration, @Nullable String targetProcessFactory) {
        if (getClusterManager().isHeadNode()) {
            return createProcessPrivileged0(configuration, targetProcessFactory);
        }

        NetworkChannel channel = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class)
                .getChannel(getClusterManager().getHeadNode().getName())
                .orElseThrow(() -> new IllegalStateException("Head node channel not connected"));
        return createProcessOnHeadNode(channel, configuration, targetProcessFactory);
    }

    @NotNull
    private static Task<ProcessInformation> createProcessOnHeadNode(@NotNull NetworkChannel channel, @NotNull ProcessFactoryConfiguration configuration, @Nullable String targetProcessFactory) {
        Task<ProcessInformation> task = new DefaultTask<>();
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(QueryManager.class)
                .sendPacketQuery(channel, new NodeToHeadNodeCreateProcess(configuration, targetProcessFactory))
                .onComplete(packet -> {
                    if (packet instanceof NodeToHeadNodeCreateProcessResult) {
                        task.complete(((NodeToHeadNodeCreateProcessResult) packet).getProcessInformation());
                    } else {
                        task.complete(null);
                    }
                });
        return task;
    }

    @NotNull
    private static Task<ProcessInformation> createProcessPrivileged0(@NotNull ProcessFactoryConfiguration configuration, @Nullable String targetProcessFactory) {
        ProcessFactoryController factoryController = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProcessFactoryController.class);
        if (targetProcessFactory == null) {
            return factoryController.getDefaultProcessFactory().buildProcessInformation(configuration);
        }

        return factoryController
                .getProcessFactoryByName(targetProcessFactory)
                .orElse(factoryController.getDefaultProcessFactory())
                .buildProcessInformation(configuration);
    }

    private static @NotNull ClusterManager getClusterManager() {
        return ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ClusterManager.class);
    }
}
