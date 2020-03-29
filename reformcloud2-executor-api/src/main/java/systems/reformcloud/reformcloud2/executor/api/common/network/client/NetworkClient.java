package systems.reformcloud.reformcloud2.executor.api.common.network.client;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;

import java.util.function.Supplier;

public interface NetworkClient {

    boolean connect(
            @NotNull String host,
            int port,
            @NotNull Supplier<NetworkChannelReader> supplier,
            @NotNull ChallengeAuthHandler challengeAuthHandler
    );

    /**
     * Disconnects all open connections
     */
    void disconnect();
}
