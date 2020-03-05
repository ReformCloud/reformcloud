package systems.reformcloud.reformcloud2.executor.api.common.network.server;

import systems.reformcloud.reformcloud2.executor.api.common.network.auth.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public interface NetworkServer {

    /**
     * Binds to the given ip:port
     *
     * @param host                 The host on which the cloud should bing
     * @param port                 The port which the cloud should use
     * @param readerHelper         The channel reader which accepts all actions coming through the channel
     * @param challengeAuthHandler The auth handler for new network components
     */
    void bind(
            @Nonnull String host,
            int port,
            @Nonnull Supplier<NetworkChannelReader> readerHelper, @Nonnull ChallengeAuthHandler challengeAuthHandler
    );

    /**
     * Closes a network server
     *
     * @param port The port of the network server which should get closed
     */
    void close(int port);

    /**
     * Closes all open network servers
     */
    void closeAll();
}
