package de.klaro.reformcloud2.executor.api.common.restapi.request;

import de.klaro.reformcloud2.executor.api.common.restapi.auth.Auth;

import java.util.List;

public interface RequestListenerHandler {

    Auth authHandler();

    RequestListenerHandler registerListener(RequestHandler requestHandler);

    RequestListenerHandler registerListener(Class<? extends RequestHandler> requestHandler);

    void unregisterHandler(RequestHandler requestHandler);

    List<RequestHandler> getHandlers();
}
