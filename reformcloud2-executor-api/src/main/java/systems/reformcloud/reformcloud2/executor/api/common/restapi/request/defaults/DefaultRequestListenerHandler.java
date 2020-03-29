package systems.reformcloud.reformcloud2.executor.api.common.restapi.request.defaults;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.Auth;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;

import java.lang.reflect.InvocationTargetException;
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
    public void setAuth(@NotNull Auth auth) {
        this.auth = auth;
    }

    @NotNull
    @Override
    public Auth authHandler() {
        return auth;
    }

    @NotNull
    @Override
    public RequestListenerHandler registerListener(@NotNull RequestHandler requestHandler) {
        requestHandlers.add(requestHandler);
        return this;
    }

    @NotNull
    @Override
    public RequestListenerHandler registerListener(@NotNull Class<? extends RequestHandler> requestHandler) {
        try {
            return registerListener(requestHandler.getDeclaredConstructor().newInstance());
        } catch (final IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public void unregisterHandler(@NotNull RequestHandler requestHandler) {
        this.requestHandlers.remove(requestHandler);
    }

    @NotNull
    @Override
    public Collection<RequestHandler> getHandlers() {
        return Collections.unmodifiableList(requestHandlers);
    }
}
