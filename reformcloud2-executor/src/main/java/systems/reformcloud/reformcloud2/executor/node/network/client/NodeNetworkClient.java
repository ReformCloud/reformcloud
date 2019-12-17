package systems.reformcloud.reformcloud2.executor.node.network.client;

import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.network.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.DefaultNetworkClient;
import systems.reformcloud.reformcloud2.executor.api.common.network.client.NetworkClient;

public class NodeNetworkClient implements NetworkClient {

  private static final NetworkClient CLIENT = new DefaultNetworkClient();

  public static final Collection<String> CONNECTIONS = new ArrayList<>();

  @Override
  public boolean connect(@Nonnull String host, int port, @Nonnull Auth auth,
                         @Nonnull NetworkChannelReader channelReader) {
    if (CONNECTIONS.stream().anyMatch(host::equals)) {
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
