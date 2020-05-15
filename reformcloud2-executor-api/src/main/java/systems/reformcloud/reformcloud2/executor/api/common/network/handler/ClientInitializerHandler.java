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

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.PacketDecoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.PacketEncoder;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.serialisation.LengthDeserializer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.netty.serialisation.LengthSerializer;

import java.util.function.Supplier;

public final class ClientInitializerHandler extends ChannelInitializer<Channel> {

    private final Supplier<NetworkChannelReader> supplier;
    private final ChallengeAuthHandler challengeAuthHandler;

    public ClientInitializerHandler(Supplier<NetworkChannelReader> supplier, ChallengeAuthHandler challengeAuthHandler) {
        this.supplier = supplier;
        this.challengeAuthHandler = challengeAuthHandler;
    }

    @Override
    protected void initChannel(Channel channel) {
        channel.pipeline()
                .addLast("decoder", new LengthDeserializer())
                .addLast("packet-decoder", new PacketDecoder())
                .addLast("encoder", new LengthSerializer())
                .addLast("packet-encoder", new PacketEncoder())
                .addLast("handler", new ChannelReaderHelper(supplier.get(), challengeAuthHandler));
    }
}
