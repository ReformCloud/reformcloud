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
package systems.reformcloud.reformcloud2.executor.api.common.network.channel.shared;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.defaults.DefaultPacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.exception.SilentNetworkException;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import java.net.InetSocketAddress;

public abstract class SharedNetworkChannelReader implements NetworkChannelReader {

    protected PacketSender packetSender;

    @NotNull
    @Override
    public PacketSender sender() {
        return this.packetSender;
    }

    @Override
    public void setChannelHandlerContext(@NotNull ChannelHandlerContext channelHandlerContext, @NotNull String name) {
        Conditions.isTrue(this.packetSender == null, "Cannot redefine packet sender");
        this.packetSender = new DefaultPacketSender(channelHandlerContext);
        this.packetSender.setName(name);
        DefaultChannelManager.INSTANCE.registerChannel(this.packetSender);
    }

    @Override
    public void channelActive(@NotNull ChannelHandlerContext context) {
        if (packetSender == null) {
            String address = ((InetSocketAddress) context.channel().remoteAddress()).getAddress().getHostAddress();
            System.out.println(LanguageManager.get("network-channel-connected", address));
        }
    }

    @Override
    public void read(@NotNull ChannelHandlerContext context, @NotNull ChallengeAuthHandler authHandler,
                     @NotNull ChannelReaderHelper parent, @NotNull Packet input) {
        NetworkUtil.EXECUTOR.execute(() -> {
            if (input.getQueryUniqueID() != null) {
                Task<Packet> waitingQuery = ExecutorAPI.getInstance().getPacketHandler().getQueryHandler().getWaitingQuery(input.getQueryUniqueID());
                if (waitingQuery != null) {
                    waitingQuery.complete(input);
                    return;
                }
            }

            try {
                input.handlePacketReceive(this, authHandler, parent, this.packetSender, context);
            } catch (final Throwable throwable) {
                System.err.println("Error while handling packet " + input.getId() + "@" + input.getClass().getName());
                throw new SilentNetworkException(throwable);
            }
        });
    }
}
