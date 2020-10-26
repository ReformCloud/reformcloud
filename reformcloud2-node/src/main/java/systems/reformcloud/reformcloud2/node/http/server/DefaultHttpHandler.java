/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
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
package systems.reformcloud.reformcloud2.node.http.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.decode.DecodeResult;
import systems.reformcloud.reformcloud2.executor.api.http.listener.HttpListenerRegistry;
import systems.reformcloud.reformcloud2.executor.api.http.listener.HttpListenerRegistryEntry;
import systems.reformcloud.reformcloud2.executor.api.http.reponse.ListeningHttpServerResponse;
import systems.reformcloud.reformcloud2.executor.api.http.request.HttpRequest;
import systems.reformcloud.reformcloud2.executor.api.http.request.HttpRequestSource;
import systems.reformcloud.reformcloud2.executor.api.http.request.RequestMethod;
import systems.reformcloud.reformcloud2.executor.api.utility.StringUtil;
import systems.reformcloud.reformcloud2.node.http.DefaultHeaders;
import systems.reformcloud.reformcloud2.node.http.cookie.CookieCoder;
import systems.reformcloud.reformcloud2.node.http.request.DefaultHttpRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultHttpHandler extends SimpleChannelInboundHandler<io.netty.handler.codec.http.HttpRequest> {

    private final HttpRequestSource requestSource;
    private final HttpListenerRegistry registry;

    public DefaultHttpHandler(HttpRequestSource requestSource, HttpListenerRegistry registry) {
        this.requestSource = requestSource;
        this.registry = registry;
    }

    @NotNull
    private static ChannelFuture sendResponse(@NotNull ListeningHttpServerResponse<?> response, @NotNull Channel channel) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
            response.httpVersion() == systems.reformcloud.reformcloud2.executor.api.http.HttpVersion.HTTP_1_0
                ? HttpVersion.HTTP_1_0
                : HttpVersion.HTTP_1_1,
            HttpResponseStatus.valueOf(response.status().code()),
            Unpooled.wrappedBuffer(response.body()),
            toNettyHeaders(response),
            new DefaultHttpHeaders()
        );
        return channel.writeAndFlush(fullHttpResponse);
    }

    private static boolean shouldHandle(@NotNull String registeredPath, @NotNull String uri) {
        String[] registeredPathParts = registeredPath.split("/");
        String[] uriParts = uri.split("/");

        if (uriParts.length != registeredPathParts.length) {
            return false;
        }

        for (int i = 0; i < registeredPathParts.length; i++) {
            String registeredPart = registeredPathParts[i];
            if ((!registeredPart.startsWith("{") || !registeredPart.endsWith("}")) && !uriParts[i].equalsIgnoreCase(registeredPart)) {
                return false;
            }
        }

        return true;
    }

    @NotNull
    private static Map<String, String> pathParameters(@NotNull String registeredPath, @NotNull String uri) {
        Map<String, String> result = new HashMap<>();

        String[] registeredPathParts = registeredPath.split("/");
        String[] uriParts = uri.split("/");

        if (uriParts.length != registeredPathParts.length) {
            return result;
        }

        for (int i = 0; i < registeredPathParts.length; i++) {
            String registeredPart = registeredPathParts[i];
            if (registeredPart.startsWith("{") && registeredPart.endsWith("}")) {
                result.put(StringUtil.replaceLastEmpty(registeredPart.replaceFirst("\\{", ""), "}"), uriParts[i]);
            }
        }

        return result;
    }

    @NotNull
    private static HttpHeaders toNettyHeaders(ListeningHttpServerResponse<?> response) {
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        for (Map.Entry<String, String> entry : response.headers().entries()) {
            httpHeaders.set(entry.getKey(), entry.getValue());
        }

        if (!response.cookies().isEmpty()) {
            httpHeaders.set(HttpHeaderNames.SET_COOKIE.toString(), CookieCoder.encode(response.cookies()));
        }

        return httpHeaders;
    }

    @NotNull
    private static RequestMethod fromNetty(@NotNull HttpMethod method) {
        if (method.equals(HttpMethod.TRACE)) {
            return RequestMethod.TRACE;
        } else if (method.equals(HttpMethod.DELETE)) {
            return RequestMethod.DELETE;
        } else if (method.equals(HttpMethod.GET)) {
            return RequestMethod.GET;
        } else if (method.equals(HttpMethod.PUT)) {
            return RequestMethod.PUT;
        } else if (method.equals(HttpMethod.PATCH)) {
            return RequestMethod.PATCH;
        } else if (method.equals(HttpMethod.OPTIONS)) {
            return RequestMethod.OPTIONS;
        } else if (method.equals(HttpMethod.POST)) {
            return RequestMethod.POST;
        } else if (method.equals(HttpMethod.HEAD)) {
            return RequestMethod.HEAD;
        } else {
            throw new IllegalStateException("Unknown request method: " + method);
        }
    }

    @NotNull
    private static byte[] readBodyFromRequest(@NotNull FullHttpRequest request) {
        if (request.content().hasArray()) {
            return request.content().array();
        } else {
            byte[] body = new byte[request.content().readableBytes()];
            request.content().getBytes(request.content().readerIndex(), body);
            return body;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (!ctx.channel().isActive() || !ctx.channel().isOpen() || !ctx.channel().isWritable()) {
            ctx.channel().close();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, io.netty.handler.codec.http.HttpRequest msg) {
        if (!msg.decoderResult().isSuccess()) {
            ctx.close();
            return;
        }

        String path = URI.create(msg.uri()).getPath();
        if (path.isEmpty()) {
            path = "/";
        } else if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        ListeningHttpServerResponse<?> response = null;
        for (Map.Entry<String, List<HttpListenerRegistryEntry>> entry : this.registry.getListeners().entrySet()) {
            if (shouldHandle(entry.getKey(), path)) {
                HttpRequest<?> request = new DefaultHttpRequest(
                    msg instanceof FullHttpRequest ? readBodyFromRequest((FullHttpRequest) msg) : new byte[0],
                    msg.protocolVersion() == HttpVersion.HTTP_1_0
                        ? systems.reformcloud.reformcloud2.executor.api.http.HttpVersion.HTTP_1_0
                        : systems.reformcloud.reformcloud2.executor.api.http.HttpVersion.HTTP_1_1,
                    new DefaultHeaders(msg.headers()),
                    msg.decoderResult().isSuccess() ? DecodeResult.success() : msg.decoderResult().isFailure()
                        ? DecodeResult.result(msg.decoderResult().cause()) : DecodeResult.unfinished(),
                    this.requestSource,
                    fromNetty(msg.method()),
                    msg.uri(),
                    path,
                    pathParameters(entry.getKey(), path)
                );
                for (HttpListenerRegistryEntry httpListener : entry.getValue()) {
                    if (Arrays.binarySearch(httpListener.getHandlingRequestMethods(), request.requestMethod()) < 0) {
                        continue;
                    }

                    response = httpListener.getListener().handleRequest(request);
                    if (response.lastHandler()) {
                        ChannelFuture channelFuture = sendResponse(response, ctx.channel());
                        if (response.closeAfterSent()) {
                            channelFuture.addListener(ChannelFutureListener.CLOSE);
                        }

                        return;
                    }
                }
            }
        }

        if (response == null) {
            ctx.channel().writeAndFlush(new DefaultFullHttpResponse(
                msg.protocolVersion(),
                HttpResponseStatus.NOT_FOUND
            )).addListener(ChannelFutureListener.CLOSE);
        } else {
            ChannelFuture channelFuture = sendResponse(response, ctx.channel());
            if (response.closeAfterSent()) {
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}
