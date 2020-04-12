package systems.reformcloud.reformcloud2.executor.api.common.restapi.request;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.Auth;

import java.util.Collection;

public interface RequestListenerHandler {

    /**
     * Updates the current auth handler
     *
     * @param auth The new auth handler
     * @throws UnsupportedOperationException If the operation is not supported by the current handler
     */
    void setAuth(@NotNull Auth auth);

    /**
     * @return The current auth handler of this instance
     */
    @NotNull
    Auth authHandler();

    /**
     * Registers a new request handler
     *
     * @param requestHandler The request handler which should get registered
     * @return The current instance of this class
     */
    @NotNull
    RequestListenerHandler registerListener(@NotNull RequestHandler requestHandler);

    /**
     * Registers a new request handler
     *
     * @see #registerListener(RequestHandler)
     * @param requestHandler The request handler which should get registered
     * @return The current instance of this class
     */
    @NotNull
    RequestListenerHandler registerListener(@NotNull Class<? extends RequestHandler> requestHandler);

    /**
     * Unregisters a request handler
     *
     * @param requestHandler The request handler which should get unregistered
     */
    void unregisterHandler(@NotNull RequestHandler requestHandler);

    /**
     * @return All currently registered request listener of this instance
     */
    @NotNull
    Collection<RequestHandler> getHandlers();
}
