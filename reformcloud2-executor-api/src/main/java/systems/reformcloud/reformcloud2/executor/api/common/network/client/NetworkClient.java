package systems.reformcloud.reformcloud2.executor.api.common.network.client;

import systems.reformcloud.reformcloud2.executor.api.common.network.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;

public interface NetworkClient {

    boolean connect(String host, int port, Auth auth, NetworkChannelReader channelReader);

    void disconnect();
}
