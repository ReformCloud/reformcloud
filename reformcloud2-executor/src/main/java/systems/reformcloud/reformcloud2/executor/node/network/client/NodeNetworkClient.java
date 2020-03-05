package systems.reformcloud.reformcloud2.executor.node.network.client;

import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class NodeNetworkClient implements NetworkClient {

    private static final NetworkClient CLIENT = new DefaultNetworkClient();

    public static final Collection<String> CONNECTIONS = new ArrayList<>();

    @Override
    public boolean connect(@Nonnull String host, int port, @Nonnull Supplier<NetworkChannelReader> supplier, @Nonnull ChallengeAuthHandler challengeAuthHandler) {
        if (CONNECTIONS.stream().anyMatch(host::equals)) {
            return false;
        }

        if (CLIENT.connect(host, port, supplier, challengeAuthHandler)) {
            CONNECTIONS.add(host);
            return true;
        }

        return false;
    }

    @Override
    public void disconnect() {
        CONNECTIONS.clear();
        CLIENT.disconnect();
    }
}
