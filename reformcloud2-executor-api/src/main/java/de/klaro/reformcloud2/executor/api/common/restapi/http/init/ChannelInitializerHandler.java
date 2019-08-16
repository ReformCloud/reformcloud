package de.klaro.reformcloud2.executor.api.common.restapi.http.init;

import de.klaro.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

abstract class ChannelInitializerHandler extends ChannelInitializer<Channel> {

    ChannelInitializerHandler(RequestListenerHandler requestListenerHandler) {
        this.requestListenerHandler = requestListenerHandler;
    }

    protected final RequestListenerHandler requestListenerHandler;
}
