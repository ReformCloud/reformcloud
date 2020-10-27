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
package systems.reformcloud.reformcloud2.node.http.request;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.request.HttpRequestSource;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.request.SocketFrameSource;
import systems.reformcloud.reformcloud2.node.http.server.ServerConstants;
import systems.reformcloud.reformcloud2.node.http.websocket.handler.WebSocketFrameHandler;
import systems.reformcloud.reformcloud2.node.http.websocket.listener.DefaultSocketFrameListenerRegistry;
import systems.reformcloud.reformcloud2.node.http.websocket.request.DefaultSocketFrameSource;

import java.net.SocketAddress;
import java.util.Optional;

public class DefaultHttpRequestSource implements HttpRequestSource {

    protected final Channel channel;
    protected final HttpRequest request;

    public DefaultHttpRequestSource(Channel channel, HttpRequest request) {
        this.channel = channel;
        this.request = request;
    }

    @Override
    public @NotNull String id() {
        return this.channel.id().asLongText();
    }

    @Override
    public boolean isOpen() {
        return this.channel.isOpen();
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }

    @Override
    public boolean isWritable() {
        return this.channel.isWritable();
    }

    @Override
    public long bytesBeforeUnwritable() {
        return this.channel.bytesBeforeUnwritable();
    }

    @Override
    public long bytesBeforeWritable() {
        return this.channel.bytesBeforeWritable();
    }

    @Override
    public @NotNull SocketAddress serverAddress() {
        return this.channel.localAddress();
    }

    @Override
    public @NotNull SocketAddress clientAddress() {
        return this.channel.remoteAddress();
    }

    @Override
    public @NotNull Optional<SocketFrameSource> upgrade() {
        WebSocketServerHandshaker handshaker = new WebSocketServerHandshakerFactory(
            this.request.uri(),
            null,
            true,
            Short.MAX_VALUE,
            false
        ).newHandshaker(this.request);
        if (handshaker == null) {
            // we don't know which version the client uses to send an unsupported version response back. (we cannot upgrade)
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(this.channel);
            return Optional.empty();
        } else {
            // we can handshake with the client so we remove our request header first as netty
            // will add a new one to decode the handshake response of the client.
            this.channel.pipeline().remove(ServerConstants.HTTP_HANDLER);
            // send the handshake request to the client
            handshaker.handshake(this.channel, this.request);
            // Now create the upgraded socket frame source
            SocketFrameSource socketFrameSource = new DefaultSocketFrameSource(this.channel, new DefaultSocketFrameListenerRegistry());
            // Switch the endpoint handler to the websocket handler
            this.channel.pipeline().addLast(ServerConstants.WEB_SOCKET_HANDLER, new WebSocketFrameHandler(socketFrameSource));
            // all done, bring the user the new socket frame source
            return Optional.of(socketFrameSource);
        }
    }

    @Override
    public void close() {
        this.channel.close();
    }
}
