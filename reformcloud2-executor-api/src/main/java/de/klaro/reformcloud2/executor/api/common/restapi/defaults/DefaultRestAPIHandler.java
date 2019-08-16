package de.klaro.reformcloud2.executor.api.common.restapi.defaults;

import de.klaro.reformcloud2.executor.api.common.configuration.Configurable;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.restapi.HttpOperation;
import de.klaro.reformcloud2.executor.api.common.restapi.RestAPIHandler;
import de.klaro.reformcloud2.executor.api.common.restapi.request.RequestHandler;
import de.klaro.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import de.klaro.reformcloud2.executor.api.common.restapi.request.WebRequester;
import de.klaro.reformcloud2.executor.api.common.utility.function.Double;
import de.klaro.reformcloud2.executor.api.common.utility.operation.Operation;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class DefaultRestAPIHandler extends RestAPIHandler {

    public DefaultRestAPIHandler(RequestListenerHandler requestHandler) {
        super(requestHandler);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame webSocketFrame) {
        try {
            Configurable configurable = new JsonConfiguration(webSocketFrame.text());
            Double<Boolean, WebRequester> result = requestHandler.authHandler().handleAuth(configurable);
            if (!result.getFirst()) {
                channelHandlerContext.channel().close();
            } else {
                Map<UUID, Operation> operations = new HashMap<>();
                requestHandler.getHandlers().forEach(new Consumer<RequestHandler>() {
                    @Override
                    public void accept(RequestHandler requestHandler) {
                        requestHandler.handleRequest(
                                result.getSecond(),
                                webSocketFrame,
                                new Consumer<WebSocketFrame>() {
                                    @Override
                                    public void accept(WebSocketFrame webSocketFrame) {
                                        Operation operation = new HttpOperation();
                                        operations.put(operation.identifier(), operation);
                                        channelHandlerContext.channel().writeAndFlush(webSocketFrame).addListener(new ChannelFutureListener() {
                                            @Override
                                            public void operationComplete(ChannelFuture channelFuture) {
                                                operations.remove(operation.identifier()).complete();
                                            }
                                        });
                                    }
                                }
                        );
                    }
                });

                channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("Completed action")).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) {
                        CompletableFuture.runAsync(new Runnable() {
                            @Override
                            public void run() {
                                while (!operations.isEmpty()) {
                                    try {
                                        Thread.sleep(0, 500000);
                                    } catch (final InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                channelFuture.channel().close();
                            }
                        });
                    }
                });
            }
        } catch (final Throwable throwable) {
            channelHandlerContext.channel().close();
        }
    }
}
