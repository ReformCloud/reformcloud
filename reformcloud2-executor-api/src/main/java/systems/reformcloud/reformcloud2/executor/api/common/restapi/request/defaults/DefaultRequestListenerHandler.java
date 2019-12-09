package systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults;

import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class DefaultRequestListenerHandler implements RequestListenerHandler {

    public DefaultRequestListenerHandler(Auth auth) {
        this.auth = auth;
    }

    private Auth auth;

    private final List<RequestHandler> requestHandlers = new ArrayList<>();

    @Override
    public void setAuth(@Nonnull Auth auth) {
        this.auth = auth;
    }

    @Nonnull
    @Override
    public Auth authHandler() {
        return auth;
    }

    @Nonnull
    @Override
    public RequestListenerHandler registerListener(@Nonnull RequestHandler requestHandler) {
        requestHandlers.add(requestHandler);
        return this;
    }

    @Nonnull
    @Override
    public RequestListenerHandler registerListener(@Nonnull Class<? extends RequestHandler> requestHandler) {
        try {
            return registerListener(requestHandler.newInstance());
        } catch (final InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public void unregisterHandler(@Nonnull RequestHandler requestHandler) {
        this.requestHandlers.remove(requestHandler);
    }

    @Nonnull
    @Override
    public Collection<RequestHandler> getHandlers() {
        return Collections.unmodifiableList(requestHandlers);
    }
}
