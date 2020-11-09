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

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import systems.reformcloud.reformcloud2.executor.api.network.channel.listener.ChannelListener;
import systems.reformcloud.reformcloud2.shared.network.handler.NettyChannelListenerWrapper;
import systems.reformcloud.reformcloud2.shared.network.handler.NettyPacketDecoder;
import systems.reformcloud.reformcloud2.shared.network.handler.NettyPacketEncoder;

import java.util.function.Supplier;

public class NettyChannelInitializer extends ChannelInitializer<Channel> {

    private final Supplier<ChannelListener> channelListenerFactory;

    public NettyChannelInitializer(Supplier<ChannelListener> channelListenerFactory) {
        this.channelListenerFactory = channelListenerFactory;
    }

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline()
            .addLast("frame-decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
            .addLast("frame-encoder", new LengthFieldPrepender(4, 0, false))
            .addLast("packet-decoder", new NettyPacketDecoder())
            .addLast("packet-encoder", new NettyPacketEncoder())
            .addLast("listener", new NettyChannelListenerWrapper(this.channelListenerFactory));
    }
}
