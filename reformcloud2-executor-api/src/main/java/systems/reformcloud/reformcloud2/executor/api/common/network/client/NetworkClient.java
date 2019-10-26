package systems.reformcloud.reformcloud2.executor.api.common.network.client;

import systems.reformcloud.reformcloud2.executor.api.common.network.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;

public interface NetworkClient {

    /**
     * Connects to a network server
     *
     * @param host The host where the connection should go to
     * @param port The port where the connection should go on
     * @param auth The auth to authenticate the current network component on the other
     * @param channelReader The channel reader which should get used after the successful auth
     * @return If the connection to the server was successful
     * @apiNote This does not return if the authentication was successful, only if the network connection could be opened
     */
    boolean connect(String host, int port, Auth auth, NetworkChannelReader channelReader);

    /**
     * Disconnects all open connections
     */
    void disconnect();
}
