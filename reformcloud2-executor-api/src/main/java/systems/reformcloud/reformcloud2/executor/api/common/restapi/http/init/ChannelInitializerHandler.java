package systems.reformcloud.reformcloud2.executor.api.common.restapi.http.init;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;

abstract class ChannelInitializerHandler extends ChannelInitializer<Channel> {

  ChannelInitializerHandler(RequestListenerHandler requestListenerHandler) {
    this.requestListenerHandler = requestListenerHandler;
  }

  final RequestListenerHandler requestListenerHandler;
}
