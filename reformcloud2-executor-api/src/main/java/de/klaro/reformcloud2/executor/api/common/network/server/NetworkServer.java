package de.klaro.reformcloud2.executor.api.common.network.server;

import de.klaro.reformcloud2.executor.api.common.network.auth.ServerAuthHandler;

public interface NetworkServer {

    void bind(String host, int port, ServerAuthHandler authHandler);

    void close(int port);

    void closeAll();
}
