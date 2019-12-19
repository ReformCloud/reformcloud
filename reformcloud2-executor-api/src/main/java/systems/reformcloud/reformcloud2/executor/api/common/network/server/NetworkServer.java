package systems.reformcloud.reformcloud2.executor.api.common.network.server;

import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.ServerAuthHandler;

public interface NetworkServer {

  /**
   * Binds to the given ip:port
   *
   * @param host The host on which the cloud should bing
   * @param port The port which the cloud should use
   * @param authHandler The auth handler for incoming connections
   */
  void bind(@Nonnull String host, int port,
            @Nonnull ServerAuthHandler authHandler);

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
