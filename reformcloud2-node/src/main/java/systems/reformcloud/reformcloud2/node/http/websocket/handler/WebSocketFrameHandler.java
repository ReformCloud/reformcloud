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
package systems.reformcloud.reformcloud2.node.http.websocket.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.SocketFrame;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.SocketFrameType;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.listener.SocketFrameListenerRegistryEntry;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.request.RequestSocketFrame;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.request.SocketFrameSource;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.response.ResponseSocketFrame;
import systems.reformcloud.reformcloud2.node.http.utils.BinaryUtils;
import systems.reformcloud.reformcloud2.node.http.websocket.DefaultCloseSocketFrame;
import systems.reformcloud.reformcloud2.node.http.websocket.DefaultContinuationSocketFrame;
import systems.reformcloud.reformcloud2.node.http.websocket.DefaultTextSocketFrame;
import systems.reformcloud.reformcloud2.node.http.websocket.TypedSocketFrame;
import systems.reformcloud.reformcloud2.node.http.websocket.request.DefaultRequestSocketFrame;

import java.io.IOException;
import java.util.Arrays;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final SocketFrameSource socketFrameSource;

    public WebSocketFrameHandler(SocketFrameSource socketFrameSource) {
        this.socketFrameSource = socketFrameSource;
    }

    @NotNull
    private static SocketFrame<?> fromNetty(WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            return new DefaultTextSocketFrame(frame.rsv(), frame.isFinalFragment(), ((TextWebSocketFrame) frame).text());
        } else if (frame instanceof CloseWebSocketFrame) {
            CloseWebSocketFrame closeFrame = (CloseWebSocketFrame) frame;
            return new DefaultCloseSocketFrame(frame.rsv(), frame.isFinalFragment(), closeFrame.statusCode(), closeFrame.reasonText());
        } else if (frame instanceof ContinuationWebSocketFrame) {
            return new DefaultContinuationSocketFrame(frame.rsv(), frame.isFinalFragment(), ((ContinuationWebSocketFrame) frame).text());
        } else if (frame instanceof PingWebSocketFrame) {
            return new TypedSocketFrame(SocketFrameType.PING, frame.rsv(), frame.isFinalFragment(), BinaryUtils.binaryArrayFromByteBuf(frame));
        } else if (frame instanceof PongWebSocketFrame) {
            return new TypedSocketFrame(SocketFrameType.PONG, frame.rsv(), frame.isFinalFragment(), BinaryUtils.binaryArrayFromByteBuf(frame));
        } else if (frame instanceof BinaryWebSocketFrame) {
            return new TypedSocketFrame(SocketFrameType.BINARY, frame.rsv(), frame.isFinalFragment(), BinaryUtils.binaryArrayFromByteBuf(frame));
        } else {
            throw new IllegalStateException("Illegal/unimplemented socket frame type: " + frame.getClass().getName());
        }
    }

    @Contract("_ -> new")
    public static @NotNull WebSocketFrame toNetty(@NotNull SocketFrame<?> frame) {
        switch (frame.type()) {
            case TEXT:
                return new TextWebSocketFrame(frame.finalFragment(), frame.rsv(), Unpooled.wrappedBuffer(frame.content()));
            case PING:
                return new PingWebSocketFrame(frame.finalFragment(), frame.rsv(), Unpooled.wrappedBuffer(frame.content()));
            case PONG:
                return new PongWebSocketFrame(frame.finalFragment(), frame.rsv(), Unpooled.wrappedBuffer(frame.content()));
            case BINARY:
                return new BinaryWebSocketFrame(frame.finalFragment(), frame.rsv(), Unpooled.wrappedBuffer(frame.content()));
            case CLOSE:
                return new CloseWebSocketFrame(frame.finalFragment(), frame.rsv(), Unpooled.wrappedBuffer(frame.content()));
            case CONTINUATION:
                return new ContinuationWebSocketFrame(frame.finalFragment(), frame.rsv(), Unpooled.wrappedBuffer(frame.content()));
            default:
                throw new IllegalStateException("Unsupported frame type: " + frame.type());
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
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        RequestSocketFrame frame = new DefaultRequestSocketFrame(fromNetty(msg), this.socketFrameSource);
        for (SocketFrameListenerRegistryEntry listener : this.socketFrameSource.listenerRegistry().getListeners()) {
            if (listener.getHandlingFrameTypes().length != 0 && Arrays.binarySearch(listener.getHandlingFrameTypes(), frame.request().type()) < 0) {
                continue;
            }

            ResponseSocketFrame<?> responseSocketFrame = listener.getListener().handleFrame(frame);
            if (responseSocketFrame != null) {
                ChannelFuture channelFuture = ctx.channel().writeAndFlush(toNetty(responseSocketFrame.response()));
                if (responseSocketFrame.closeAfterSent()) {
                    channelFuture.addListener(ChannelFutureListener.CLOSE);
                    return;
                }

                if (responseSocketFrame.lastHandler()) {
                    return;
                }
            }
        }
    }
}
