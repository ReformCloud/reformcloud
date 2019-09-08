package de.klaro.reformcloud2.executor.api.common.network.client;

import de.klaro.reformcloud2.executor.api.common.network.auth.Auth;
import de.klaro.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;

public interface NetworkClient {

    boolean connect(String host, int port, Auth auth, NetworkChannelReader channelReader);

    void disconnect();
}
