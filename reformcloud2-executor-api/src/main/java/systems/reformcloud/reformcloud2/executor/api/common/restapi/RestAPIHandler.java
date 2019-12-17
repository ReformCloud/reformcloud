package systems.reformcloud.reformcloud2.executor.api.common.restapi;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import javax.annotation.Nonnull;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;

public abstract class RestAPIHandler
    extends SimpleChannelInboundHandler<HttpRequest> {

  public RestAPIHandler(@Nonnull RequestListenerHandler requestHandler) {
    this.requestHandler = requestHandler;
  }

  protected final RequestListenerHandler requestHandler;
}
