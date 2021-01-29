/*
 * This file is part of reformcloud, licensed under the MIT License (MIT).
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
package systems.reformcloud.shared.network.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.network.data.ProtocolBuffer;
import systems.reformcloud.network.packet.PacketProvider;
import systems.reformcloud.shared.network.data.DefaultProtocolBuffer;

import java.util.List;

public class NettyPacketDecoder extends MessageToMessageDecoder<ByteBuf> {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
    if (ctx.channel().isActive() && msg.isReadable()) {
      ProtocolBuffer buffer = new DefaultProtocolBuffer(msg);
      ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(PacketProvider.class).getPacketById(buffer.readVarInt()).ifPresent(packet -> {
        packet.setQueryUniqueID(buffer.readUniqueId());
        packet.read(buffer);
        out.add(packet);
      });
    }
  }
}
