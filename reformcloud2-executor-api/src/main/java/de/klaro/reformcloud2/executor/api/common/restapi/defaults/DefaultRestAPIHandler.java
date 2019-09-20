package de.klaro.reformcloud2.executor.api.common.restapi.defaults;

import de.klaro.reformcloud2.executor.api.common.configuration.Configurable;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.restapi.HttpOperation;
import de.klaro.reformcloud2.executor.api.common.restapi.RestAPIHandler;
import de.klaro.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import de.klaro.reformcloud2.executor.api.common.restapi.request.WebRequester;
import de.klaro.reformcloud2.executor.api.common.utility.function.Double;
import de.klaro.reformcloud2.executor.api.common.utility.operation.Operation;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class DefaultRestAPIHandler extends RestAPIHandler {

    public DefaultRestAPIHandler(RequestListenerHandler requestHandler) {
        super(requestHandler);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame webSocketFrame) {
        try {
            Configurable configurable = new JsonConfiguration(webSocketFrame.text());
            Double<Boolean, WebRequester> result = requestHandler.authHandler().handleAuth(configurable, channelHandlerContext);
            if (!result.getFirst()) {
                channelHandlerContext.channel().close();
            } else {
                Map<UUID, Operation> operations = new HashMap<>();
                requestHandler.getHandlers().forEach(requestHandler -> requestHandler.handleRequest(
                        result.getSecond(),
                        webSocketFrame,
                        webSocketFrame1 -> {
                            Operation operation = new HttpOperation();
                            operations.put(operation.identifier(), operation);
                            channelHandlerContext.channel().writeAndFlush(webSocketFrame1).addListener((ChannelFutureListener) channelFuture -> operations.remove(operation.identifier()).complete());
                        }
                ));

                channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("Completed action")).addListener((ChannelFutureListener) channelFuture -> CompletableFuture.runAsync(() -> {
                    while (!operations.isEmpty()) {
                        try {
                            Thread.sleep(0, 500000);
                        } catch (final InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                    channelFuture.channel().close();
                }));
            }
        } catch (final Throwable throwable) {
            channelHandlerContext.channel().close();
        }
    }
}
