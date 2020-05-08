/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.executor.node.api.message;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.MessageAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.MessageSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ReceiverType;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.NamedMessagePacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.ProxiedChannelMessage;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.TypeMessagePacket;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class ChannelMessageAPIImplementation implements MessageSyncAPI, MessageAsyncAPI {

    @NotNull
    @Override
    public Task<Void> sendChannelMessageAsync(@NotNull JsonConfiguration jsonConfiguration, @NotNull String baseChannel,
                                              @NotNull String subChannel, @NotNull ErrorReportHandling errorReportHandling, String @NotNull ... receivers) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Arrays.stream(receivers).forEach(receiver -> DefaultChannelManager.INSTANCE.get(receiver).orElseDo(Objects::nonNull, () -> {
                switch (errorReportHandling) {
                    case PRINT_ERROR: {
                        System.err.println(LanguageManager.get("message-api-network-sender-not-available", receiver));
                        break;
                    }

                    case THROW_EXCEPTION: {
                        throw new RuntimeException(LanguageManager.get("message-api-network-sender-not-available", receiver));
                    }

                    case NOTHING:
                    default:
                        break;
                }
            }, sender -> {
                if (NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getNode(sender.getName()) != null) {
                    sender.sendPacket(new NamedMessagePacket(Arrays.asList(receivers), jsonConfiguration, errorReportHandling, baseChannel, subChannel));
                    return;
                }

                sender.sendPacket(new ProxiedChannelMessage(jsonConfiguration, baseChannel, subChannel));
            }));
            task.complete(null);
        });
        return task;
    }

    @NotNull
    @Override
    public Task<Void> sendChannelMessageAsync(@NotNull JsonConfiguration configuration, @NotNull String baseChannel, @NotNull String subChannel, ReceiverType @NotNull ... receiverTypes) {
        Task<Void> task = new DefaultTask<>();
        Task.EXECUTOR.execute(() -> {
            Collection<NetworkChannel> channels = new ArrayList<>();
            Arrays.stream(receiverTypes).forEach(type -> {
                switch (type) {
                    case PROXY: {
                        Streams.allOf(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses(),
                                e -> !e.getProcessDetail().getTemplate().getVersion().isServer()
                        ).stream()
                                .map(e -> DefaultChannelManager.INSTANCE.get(e.getProcessDetail().getName()))
                                .filter(ReferencedOptional::isPresent)
                                .map(ReferencedOptional::get)
                                .forEach(channels::add);
                        break;
                    }

                    case SERVER: {
                        Streams.allOf(ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getAllProcesses(),
                                e -> e.getProcessDetail().getTemplate().getVersion().isServer()
                        ).stream()
                                .map(e -> DefaultChannelManager.INSTANCE.get(e.getProcessDetail().getName()))
                                .filter(ReferencedOptional::isPresent)
                                .map(ReferencedOptional::get)
                                .forEach(channels::add);
                        break;
                    }

                    case OTHERS: {
                        NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getConnectedNodes()
                                .stream()
                                .map(e -> DefaultChannelManager.INSTANCE.get(e.getName()))
                                .filter(ReferencedOptional::isPresent)
                                .map(ReferencedOptional::get)
                                .forEach(channels::add);
                        break;
                    }
                }
            });
            channels.forEach(e -> {
                if (NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getNode(e.getName()) != null) {
                    e.sendPacket(new TypeMessagePacket(Arrays.asList(receiverTypes), configuration, baseChannel, subChannel));
                    return;
                }

                e.sendPacket(new ProxiedChannelMessage(configuration, baseChannel, subChannel));
            });
            task.complete(null);
        });
        return task;
    }

    @Override
    public void sendChannelMessageSync(@NotNull JsonConfiguration jsonConfiguration, @NotNull String baseChannel, @NotNull String subChannel, @NotNull ErrorReportHandling errorReportHandling, String @NotNull ... receivers) {
        sendChannelMessageAsync(jsonConfiguration, baseChannel, subChannel, errorReportHandling, receivers).awaitUninterruptedly();
    }

    @Override
    public void sendChannelMessageSync(@NotNull JsonConfiguration configuration, @NotNull String baseChannel, @NotNull String subChannel, ReceiverType @NotNull ... receiverTypes) {
        sendChannelMessageAsync(configuration, baseChannel, subChannel, receiverTypes).awaitUninterruptedly();
    }
}
