package systems.reformcloud.reformcloud2.executor.node.network.client;

import systems.reformcloud.reformcloud2.executor.api.common.network.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;

import java.util.ArrayList;
import java.util.Collection;

public class NodeNetworkClient implements NetworkClient {

    private static final NetworkClient CLIENT = new DefaultNetworkClient();

    private static final Collection<String> CONNECTIONS = new ArrayList<>();

    @Override
    public boolean connect(String host, int port, Auth auth, NetworkChannelReader channelReader) {
        if (CONNECTIONS.stream().anyMatch(e -> e.equals(host))) {
            return false;
        }

        if (CLIENT.connect(host, port, auth, channelReader)) {
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
