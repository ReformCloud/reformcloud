package systems.reformcloud.reformcloud2.executor.node.api.message;

import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.MessageAsyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.MessageSyncAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.messaging.DefaultMessageJsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.defaults.DefaultTask;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.messaging.ProxiedChannelMessage;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

public class ChannelMessageAPIImplementation implements MessageSyncAPI, MessageAsyncAPI {

    @Nonnull
    @Override
    public Task<Void> sendChannelMessageAsync(@Nonnull JsonConfiguration jsonConfiguration, @Nonnull ErrorReportHandling errorReportHandling, @Nonnull String... receivers) {
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
                    sender.sendPacket(new DefaultMessageJsonPacket(jsonConfiguration, Arrays.asList(receivers), errorReportHandling));
                    return;
                }

                sender.sendPacket(new ProxiedChannelMessage(jsonConfiguration));
            }));
            task.complete(null);
        });
        return task;
    }

    @Override
    public void sendChannelMessageSync(@Nonnull JsonConfiguration jsonConfiguration, @Nonnull ErrorReportHandling errorReportHandling, @Nonnull String... receivers) {
        sendChannelMessageAsync(jsonConfiguration, errorReportHandling, receivers).awaitUninterruptedly();
    }
}
