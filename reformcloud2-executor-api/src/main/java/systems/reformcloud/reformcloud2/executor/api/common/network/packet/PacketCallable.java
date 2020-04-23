package systems.reformcloud.reformcloud2.executor.api.common.network.packet;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;

@FunctionalInterface
public interface PacketCallable {

    void call(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler,
              @NotNull ChannelReaderHelper parent, @NotNull PacketSender sender) throws Throwable;
}
