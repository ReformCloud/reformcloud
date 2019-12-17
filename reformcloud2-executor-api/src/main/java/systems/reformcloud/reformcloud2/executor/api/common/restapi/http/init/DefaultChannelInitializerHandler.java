package systems.reformcloud.reformcloud2.executor.api.common.restapi.http.init;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.defaults.DefaultRestAPIHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;

public final class DefaultChannelInitializerHandler
    extends ChannelInitializerHandler {

  public DefaultChannelInitializerHandler(
      RequestListenerHandler requestListenerHandler) {
    super(requestListenerHandler);
  }

  @Override
  protected void initChannel(Channel channel) {
    channel.pipeline()
        .addLast(new HttpServerCodec())
        .addLast(new HttpObjectAggregator(65535))
        .addLast(new WebSocketServerProtocolHandler("/"))
        .addLast(new DefaultRestAPIHandler(requestListenerHandler));
  }
}
