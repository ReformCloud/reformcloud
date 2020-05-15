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
package systems.reformcloud.reformcloud2.executor.api.common.network.channel.defaults;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.net.InetSocketAddress;
import java.util.UUID;

public class DefaultPacketSender extends PacketSender {

    private final ChannelHandlerContext channel;
    private final long connectionTime;
    private String name;

    public DefaultPacketSender(ChannelHandlerContext channel) {
        this.channel = channel;
        this.connectionTime = System.currentTimeMillis();
    }

    @Override
    public long getConnectionTime() {
        return connectionTime;
    }

    @NotNull
    @Override
    public String getAddress() {
        return getEthernetAddress().getAddress().getHostAddress();
    }

    @NotNull
    @Override
    public InetSocketAddress getEthernetAddress() {
        return (InetSocketAddress) channel.channel().remoteAddress();
    }

    @Override
    public boolean isLoopBackSender() {
        return !getEthernetAddress().getAddress().isLoopbackAddress();
    }

    @Override
    public void sendPacket(@NotNull Object packet) {
        if (isConnected()) {
            channel.writeAndFlush(packet);
        }
    }

    @Override
    public void sendPacketSync(@NotNull Object packet) {
        if (isConnected()) {
            channel.writeAndFlush(packet).syncUninterruptibly();
        }
    }

    @Override
    public void sendPackets(@NotNull Object... packets) {
        if (isConnected()) {
            for (Object packet : packets) {
                sendPacket(packet);
            }
        }
    }

    @Override
    public void sendPacketsSync(@NotNull Object... packets) {
        if (isConnected()) {
            for (Object packet : packets) {
                sendPacketSync(packet);
            }
        }
    }

    @Override
    public void sendQueryResult(@Nullable UUID queryUniqueID, @NotNull Packet result) {
        if (queryUniqueID == null) {
            return;
        }

        ExecutorAPI.getInstance().getPacketHandler().getQueryHandler().sendQueryResultAsync(this, queryUniqueID, result);
    }

    @Override
    public boolean isConnected() {
        return channel != null && channel.channel().isOpen();
    }

    @Override
    public void close() {
        channel.close();
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(@NotNull String newName) {
        this.name = newName;
    }

    @NotNull
    @Override
    public ChannelHandlerContext getChannelContext() {
        return this.channel;
    }
}
