package systems.reformcloud.reformcloud2.executor.api.common.restapi.request;

import java.util.Collection;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.auth.Auth;

public interface RequestListenerHandler {

  /**
   * Updates the current auth handler
   *
   * @param auth The new auth handler
   * @throws UnsupportedOperationException If the operation is not supported by
   *     the current handler
   */
  void setAuth(@Nonnull Auth auth);

  /**
   * @return The current auth handler of this instance
   */
  @Nonnull Auth authHandler();

  /**
   * Registers a new request handler
   *
   * @param requestHandler The request handler which should get registered
   * @return The current instance of this class
   */
  @Nonnull
  RequestListenerHandler
  registerListener(@Nonnull RequestHandler requestHandler);

  /**
   * Registers a new request handler
   *
   * @see #registerListener(RequestHandler)
   * @param requestHandler The request handler which should get registered
   * @return The current instance of this class
   */
  @Nonnull
  RequestListenerHandler
  registerListener(@Nonnull Class<? extends RequestHandler> requestHandler);

  /**
   * Unregisters a request handler
   *
   * @param requestHandler The request handler which should get unregistered
   */
  void unregisterHandler(@Nonnull RequestHandler requestHandler);

  /**
   * @return All currently registered request listener of this instance
   */
  @Nonnull Collection<RequestHandler> getHandlers();
}
