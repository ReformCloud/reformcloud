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
package systems.reformcloud.reformcloud2.shared.network.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.network.channel.EndpointChannelReader;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.manager.ChannelManager;
import systems.reformcloud.reformcloud2.executor.api.network.netty.NettyChannelEndpoint;
import systems.reformcloud.reformcloud2.executor.api.network.netty.frame.VarInt21FrameDecoder;
import systems.reformcloud.reformcloud2.executor.api.network.netty.frame.VarInt21FrameEncoder;
import systems.reformcloud.reformcloud2.executor.api.network.netty.serialisation.PacketSerializerEncoder;
import systems.reformcloud.reformcloud2.executor.api.network.netty.serialisation.SerializedPacketDecoder;

import java.util.function.Supplier;

public final class ServerChannelInitializer extends ChannelInitializer<Channel> {

    private final Supplier<EndpointChannelReader> reader;

    public ServerChannelInitializer(Supplier<EndpointChannelReader> reader) {
        this.reader = reader;
    }

    @Override
    protected void initChannel(Channel channel) {
        NetworkChannel networkChannel = ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ChannelManager.class).createChannel(channel);

        channel.pipeline()
            .addLast("deserializer", new VarInt21FrameDecoder())
            .addLast("decoder", new SerializedPacketDecoder())
            .addLast("serializer", new VarInt21FrameEncoder())
            .addLast("encoder", new PacketSerializerEncoder())
            .addLast("handler", new NettyChannelEndpoint(this.reader.get().setNetworkChannel(networkChannel)));
    }
}
