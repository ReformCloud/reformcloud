package systems.reformcloud.reformcloud2.executor.api.common.restapi.defaults;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.Configurable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.HttpOperation;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.RestAPIHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.Double;
import systems.reformcloud.reformcloud2.executor.api.common.utility.operation.Operation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static io.netty.channel.ChannelFutureListener.CLOSE;
import static systems.reformcloud.reformcloud2.executor.api.common.restapi.request.HandlingRequestType.HTTP_REQUEST;
import static systems.reformcloud.reformcloud2.executor.api.common.restapi.request.HandlingRequestType.WEBSOCKET_FRAME;

public final class DefaultRestAPIHandler extends RestAPIHandler {

    public DefaultRestAPIHandler(RequestListenerHandler requestHandler) {
        super(requestHandler);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) {
        try {
            if (object instanceof TextWebSocketFrame) {
                handleWebSocketFrame(channelHandlerContext, (TextWebSocketFrame) object);
            } else if (object instanceof HttpRequest) {
                handleHttpRequest(channelHandlerContext, (HttpRequest) object);
            }
        } catch (final Throwable throwable) {
            channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("An error occurred: " + throwable.getMessage())).addListener(CLOSE);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame webSocketFrame) {
        Double<Boolean, WebRequester> result = tryAuth(channelHandlerContext, new JsonConfiguration(webSocketFrame.text()));
        if (!result.getFirst()) {
            channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("Authentication failed")).addListener(CLOSE);
        } else {
            Map<UUID, Operation> operations = new HashMap<>();
            requestHandler.getHandlers().stream().filter(e -> e.handlingRequestType().equals(WEBSOCKET_FRAME)).forEach(requestHandler -> requestHandler.handleRequest(
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
    }

    private void handleHttpRequest(ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest) {
        HttpHeaders httpHeaders = httpRequest.headers();
        if (!httpHeaders.contains("-XUser") || !httpHeaders.contains("-XToken")) {
            channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("Authentication failed")).addListener(CLOSE);
            return;
        }

        Configurable configurable = new JsonConfiguration()
                .add("name", httpHeaders.get("-XUser"))
                .add("token", httpHeaders.get("-XToken"));
        Double<Boolean, WebRequester> auth = tryAuth(channelHandlerContext, configurable);
        if (!auth.getFirst()) {
            channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("Authentication failed")).addListener(CLOSE);
            return;
        }

        Map<UUID, Operation> operations = new HashMap<>();
        requestHandler.getHandlers().stream().filter(e -> e.handlingRequestType().equals(HTTP_REQUEST)).forEach(requestHandler -> requestHandler.handleRequest(
                auth.getSecond(),
                httpRequest,
                httpResult -> {
                    Operation operation = new HttpOperation();
                    operations.put(operation.identifier(), operation);
                    channelHandlerContext.channel().writeAndFlush(httpResult).addListener((ChannelFutureListener) channelFuture -> operations.remove(operation.identifier()).complete());
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

    private Double<Boolean, WebRequester> tryAuth(ChannelHandlerContext channelHandlerContext, Configurable configurable) {
        return requestHandler.authHandler().handleAuth(configurable, channelHandlerContext);
    }
}
