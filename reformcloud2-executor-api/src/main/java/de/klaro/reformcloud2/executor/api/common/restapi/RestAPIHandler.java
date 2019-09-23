package de.klaro.reformcloud2.executor.api.common.restapi;

import de.klaro.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public abstract class RestAPIHandler extends SimpleChannelInboundHandler<Object> {

    public RestAPIHandler(RequestListenerHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    protected final RequestListenerHandler requestHandler;
}
