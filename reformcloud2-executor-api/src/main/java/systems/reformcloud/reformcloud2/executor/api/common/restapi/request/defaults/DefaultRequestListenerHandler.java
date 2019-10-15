package systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults;

import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class DefaultRequestListenerHandler implements RequestListenerHandler {

    public DefaultRequestListenerHandler(Auth auth) {
        this.auth = auth;
    }

    private final Auth auth;

    private final List<RequestHandler> requestHandlers = new ArrayList<>();

    @Override
    public Auth authHandler() {
        return auth;
    }

    @Override
    public RequestListenerHandler registerListener(RequestHandler requestHandler) {
        requestHandlers.add(requestHandler);
        return this;
    }

    @Override
    public RequestListenerHandler registerListener(Class<? extends RequestHandler> requestHandler) {
        try {
            return registerListener(requestHandler.newInstance());
        } catch (final InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public void unregisterHandler(RequestHandler requestHandler) {
        this.requestHandlers.remove(requestHandler);
    }

    @Override
    public Collection<RequestHandler> getHandlers() {
        return Collections.unmodifiableList(requestHandlers);
    }
}
