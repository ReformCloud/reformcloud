package systems.reformcloud.reformcloud2.executor.api.common.network.client;

import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public interface NetworkClient {

    boolean connect(
            @Nonnull String host,
            int port,
            @Nonnull Supplier<NetworkChannelReader> supplier,
            @Nonnull ChallengeAuthHandler challengeAuthHandler
    );

    /**
     * Disconnects all open connections
     */
    void disconnect();
}
