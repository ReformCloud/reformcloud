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
package systems.reformcloud.reformcloud2.executor.api.common.network.handler;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.PacketDecoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.PacketEncoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.serialisation.LengthDeserializer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.serialisation.LengthSerializer;

import java.util.function.Supplier;

public final class ServerInitializerHandler extends ChannelInitializer<Channel> {

    private final ChallengeAuthHandler authHandler;
    private final Supplier<NetworkChannelReader> reader;

    public ServerInitializerHandler(Supplier<NetworkChannelReader> reader, ChallengeAuthHandler handler) {
        this.reader = reader;
        this.authHandler = handler;
    }

    @Override
    protected void initChannel(Channel channel) {
        channel.config().setAllocator(PooledByteBufAllocator.DEFAULT);
        channel.config().setWriteBufferWaterMark(NetworkUtil.WATER_MARK);

        channel.pipeline()
                .addLast("deserializer", new LengthDeserializer())
                .addLast("decoder", new PacketDecoder())
                .addLast("serializer", new LengthSerializer())
                .addLast("encoder", new PacketEncoder())
                .addLast("handler", new ChannelReaderHelper(this.reader.get(), this.authHandler));
    }
}
