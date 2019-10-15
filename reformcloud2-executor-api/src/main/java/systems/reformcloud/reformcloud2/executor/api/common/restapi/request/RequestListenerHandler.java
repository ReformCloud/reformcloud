package systems.reformcloud.reformcloud2.executor.api.common.restapi.request;

import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.Auth;

import java.util.Collection;

public interface RequestListenerHandler {

    Auth authHandler();

    RequestListenerHandler registerListener(RequestHandler requestHandler);

    RequestListenerHandler registerListener(Class<? extends RequestHandler> requestHandler);

    void unregisterHandler(RequestHandler requestHandler);

    Collection<RequestHandler> getHandlers();
}
