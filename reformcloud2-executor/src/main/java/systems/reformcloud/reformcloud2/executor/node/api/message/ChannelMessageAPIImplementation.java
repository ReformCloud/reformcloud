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
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.TypeMessagePacket;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.messaging.ProxiedChannelMessage;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class ChannelMessageAPIImplementation implements MessageSyncAPI, MessageAsyncAPI {

    @NotNull
    @Override
    public Task<Void> sendChannelMessageAsync(@NotNull JsonConfiguration jsonConfiguration, @NotNull String baseChannel,
                                              @NotNull String subChannel, @NotNull ErrorReportHandling errorReportHandling, @NotNull String... receivers) {
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
                    sender.sendPacket(new TypeMessagePacket(jsonConfiguration, Arrays.asList(receivers), errorReportHandling, baseChannel, subChannel));
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
    public Task<Void> sendChannelMessageAsync(@NotNull JsonConfiguration configuration, @NotNull String baseChannel, @NotNull String subChannel, @NotNull ReceiverType... receiverTypes) {
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
    public void sendChannelMessageSync(@NotNull JsonConfiguration jsonConfiguration, @NotNull String baseChannel, @NotNull String subChannel, @NotNull ErrorReportHandling errorReportHandling, @NotNull String... receivers) {
        sendChannelMessageAsync(jsonConfiguration, baseChannel, subChannel, errorReportHandling, receivers).awaitUninterruptedly();
    }

    @Override
    public void sendChannelMessageSync(@NotNull JsonConfiguration configuration, @NotNull String baseChannel, @NotNull String subChannel, @NotNull ReceiverType... receiverTypes) {
        sendChannelMessageAsync(configuration, baseChannel, subChannel, receiverTypes).awaitUninterruptedly();
    }
}
