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
package systems.reformcloud.reformcloud2.node.http.websocket.request;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.CloseSocketFrame;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.SocketFrame;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.listener.SocketFrameListenerRegistry;
import systems.reformcloud.reformcloud2.executor.api.http.websocket.request.SocketFrameSource;
import systems.reformcloud.reformcloud2.node.http.request.DefaultHttpRequestSource;
import systems.reformcloud.reformcloud2.node.http.websocket.handler.WebSocketFrameHandler;

import java.util.Optional;
import java.util.concurrent.Future;

public class DefaultSocketFrameSource extends DefaultHttpRequestSource implements SocketFrameSource {

  protected final SocketFrameListenerRegistry registry;

  public DefaultSocketFrameSource(Channel channel, SocketFrameListenerRegistry registry) {
    super(channel, null);
    this.registry = registry;
  }

  @Override
  public @NotNull Future<Void> sendFrame(@NotNull SocketFrame<?> socketFrame) {
    return this.channel.writeAndFlush(WebSocketFrameHandler.toNetty(socketFrame));
  }

  @Override
  public @NotNull SocketFrameListenerRegistry listenerRegistry() {
    return this.registry;
  }

  @Override
  public void close(int statusCode, @NotNull String statusText) {
    this.channel.writeAndFlush(new CloseWebSocketFrame(statusCode, statusText)).addListener(ChannelFutureListener.CLOSE);
  }

  @Override
  public void close() {
    this.close(CloseSocketFrame.CloseStatus.NORMAL_CLOSE.code(), "bye");
  }

  @Override
  public @NotNull Optional<SocketFrameSource> upgrade() {
    return Optional.of(this);
  }
}
