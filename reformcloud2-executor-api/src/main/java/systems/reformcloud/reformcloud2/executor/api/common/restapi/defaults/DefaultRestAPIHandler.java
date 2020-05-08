/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.executor.api.common.restapi.defaults;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.RestAPIHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.RequestListenerHandler;
import systems.reformcloud.reformcloud2.executor.api.common.restapi.request.WebRequester;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Duo;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

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

        JsonConfiguration configurable = new JsonConfiguration()
                .add("name", httpHeaders.get("-XUser"))
                .add("token", httpHeaders.get("-XToken"));
        Duo<Boolean, WebRequester> auth = tryAuth(channelHandlerContext, configurable);
        if (!auth.getFirst() || auth.getSecond() == null) {
            channelHandlerContext.channel().writeAndFlush(new DefaultFullHttpResponse(
                    httpRequest.protocolVersion(),
                    HttpResponseStatus.UNAUTHORIZED
            )).addListener(CLOSE);
            return;
        }

        Collection<UUID> collection = new CopyOnWriteArrayList<>();
        Streams.allOf(requestHandler.getHandlers(), e -> e.path().equals(path) && e.canAccess(auth.getSecond())).forEach(e -> e.handleRequest(auth.getSecond(), httpRequest, httpResponse -> {
            UUID next = UUID.randomUUID();
            collection.add(next);
            channelHandlerContext.channel().writeAndFlush(
                    httpResponse
            ).addListener((ChannelFutureListener) channelFuture -> collection.remove(next));
        }));

        channelHandlerContext.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> CompletableFuture.runAsync(() -> {
            while (!collection.isEmpty()) {
                try {
                    Thread.sleep(0, 500000);
                } catch (final InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            channelFuture.channel().close();
        }));
    }

    private Duo<Boolean, WebRequester> tryAuth(ChannelHandlerContext channelHandlerContext, JsonConfiguration configurable) {
        return requestHandler.authHandler().handleAuth(configurable, channelHandlerContext);
    }
}
