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
package systems.reformcloud.reformcloud2.shared.network.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCounted;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.listener.ChannelListener;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.shared.network.channel.DefaultNetworkChannel;

import java.util.function.Supplier;

public class NettyChannelListenerWrapper extends ChannelInboundHandlerAdapter {

  private final Supplier<ChannelListener> channelListenerFactory;
  private NetworkChannel networkChannel;

  public NettyChannelListenerWrapper(Supplier<ChannelListener> channelListenerFactory) {
    this.channelListenerFactory = channelListenerFactory;
  }

  private static void release(@NotNull Object msg) {
    if (msg instanceof ReferenceCounted) {
      ReferenceCounted referenceCounted = (ReferenceCounted) msg;
      if (referenceCounted.refCnt() > 0) {
        referenceCounted.release(referenceCounted.refCnt());
      }
    }
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    if (this.networkChannel == null) {
      this.networkChannel = new DefaultNetworkChannel(ctx.channel());
      this.networkChannel.setListener(this.channelListenerFactory.get());
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    if (this.networkChannel != null) {
      this.networkChannel.getListener().channelInactive(this.networkChannel);
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (this.networkChannel != null && msg instanceof Packet) {
      Packet packet = (Packet) msg;
      if (this.networkChannel.getListener().shouldHandle(packet)) {
        this.networkChannel.getListener().handle(packet);
      }
    } else {
      release(msg);
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    if (this.networkChannel != null) {
      this.networkChannel.getListener().readOperationCompleted(this.networkChannel);
    }
  }

  @Override
  public void channelWritabilityChanged(ChannelHandlerContext ctx) {
    if (this.networkChannel != null) {
      this.networkChannel.getListener().channelWriteAbilityChanged(this.networkChannel);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    if (this.networkChannel != null) {
      this.networkChannel.getListener().exceptionCaught(this.networkChannel, cause);
    }
  }
}
