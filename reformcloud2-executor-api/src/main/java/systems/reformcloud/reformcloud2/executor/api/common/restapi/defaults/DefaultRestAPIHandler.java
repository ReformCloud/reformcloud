package systems.reformcloud.reformcloud2.executor.api.common.restapi.defaults;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.Configurable;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.HttpOperation;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.RestAPIHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.utility.function.Double;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.operation.Operation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static io.netty.channel.ChannelFutureListener.CLOSE;

public final class DefaultRestAPIHandler extends RestAPIHandler {

    public DefaultRestAPIHandler(RequestListenerHandler requestHandler) {
        super(requestHandler);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest) {
        try {
            String requestUri = new URI(httpRequest.uri()).getRawPath();
            handleHttpRequest(channelHandlerContext, requestUri, httpRequest);
        } catch (final URISyntaxException ex) {
            channelHandlerContext.writeAndFlush(
                    new DefaultFullHttpResponse(httpRequest.protocolVersion(),
                            HttpResponseStatus.NOT_FOUND,
                            Unpooled.wrappedBuffer("404 Page is not available!".getBytes()))
            ).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext channelHandlerContext, String path, HttpRequest httpRequest) {
        HttpHeaders httpHeaders = httpRequest.headers();
        if (!httpHeaders.contains("-XUser") || !httpHeaders.contains("-XToken")) {
            channelHandlerContext.channel().writeAndFlush(new DefaultFullHttpResponse(
                    httpRequest.protocolVersion(),
                    HttpResponseStatus.UNAUTHORIZED
            )).addListener(CLOSE);
            return;
        }

        Configurable configurable = new JsonConfiguration()
                .add("name", httpHeaders.get("-XUser"))
                .add("token", httpHeaders.get("-XToken"));
        Double<Boolean, WebRequester> auth = tryAuth(channelHandlerContext, configurable);
        if (!auth.getFirst()) {
            channelHandlerContext.channel().writeAndFlush(new DefaultFullHttpResponse(
                    httpRequest.protocolVersion(),
                    HttpResponseStatus.UNAUTHORIZED
            )).addListener(CLOSE);
            return;
        }

        Map<UUID, Operation> operations = new HashMap<>();
        Links.allOf(requestHandler.getHandlers(), e -> e.path().equals(path)).forEach(e -> e.handleRequest(auth.getSecond(), httpRequest, httpResponse -> {
            Operation operation = new HttpOperation();
            operations.put(operation.identifier(), operation);
            channelHandlerContext.channel().writeAndFlush(
                    httpResponse
            ).addListener((ChannelFutureListener) channelFuture -> operations.remove(operation.identifier()).complete());
        }));

        channelHandlerContext.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> CompletableFuture.runAsync(() -> {
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
