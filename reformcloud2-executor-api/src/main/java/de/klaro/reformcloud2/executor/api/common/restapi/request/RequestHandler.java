package de.klaro.reformcloud2.executor.api.common.restapi.request;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.function.Consumer;

public interface RequestHandler {

    void handleRequest(WebRequester webRequester, TextWebSocketFrame webSocketFrame, Consumer<WebSocketFrame> response);
}
