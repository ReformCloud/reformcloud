package systems.reformcloud.reformcloud2.executor.api.common.restapi.request;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public interface RequestHandler {

  /**
   * @return The path of the the web handler
   */
  String path();

  /**
   * Handles the request
   *
   * @param webRequester The web requester of this http request
   * @param in The http request itself
   * @param response The response handler of any response which should get sent
   *     to the requester
   */
  void handleRequest(@Nonnull WebRequester webRequester,
                     @Nonnull HttpRequest in,
                     @Nonnull Consumer<HttpResponse> response);

  /**
   * Checks if a web requester has access to the current handler
   *
   * @param requester The requester who want's access to this handler
   * @return If the web requester is permitted to use this request handler
   */
  boolean canAccess(@Nonnull WebRequester requester);
}
