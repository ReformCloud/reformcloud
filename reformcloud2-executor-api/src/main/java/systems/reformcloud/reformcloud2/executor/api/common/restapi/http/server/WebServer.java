package systems.reformcloud.reformcloud2.executor.api.common.restapi.http.server;

import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;

public interface WebServer {

    void add(String host, int port, RequestListenerHandler requestListenerHandler);

    void closeFuture(int port);

    void close();
}
