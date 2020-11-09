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
package systems.reformcloud.reformcloud2.shared.network.channel;

import io.netty.channel.Channel;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.address.NetworkAddress;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.channel.listener.ChannelListener;
import systems.reformcloud.reformcloud2.shared.Constants;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledExecutorService;

public class DefaultNetworkChannel extends DefaultPacketSender implements NetworkChannel {

    private NetworkAddress localAddress;
    private NetworkAddress remoteAddress;
    private Channel channel;
    private String name = Constants.EMPTY_STRING;
    private ChannelListener channelListener;

    public DefaultNetworkChannel() {
    }

    public DefaultNetworkChannel(Channel channel) {
        super(channel);
        this.setChannel(channel);
    }

    protected void setChannel(@NotNull Channel channel) {
        this.channel = super.channel = channel;
        this.localAddress = NetworkAddress.fromInetSocketAddress((InetSocketAddress) channel.localAddress());
        this.remoteAddress = NetworkAddress.fromInetSocketAddress((InetSocketAddress) channel.remoteAddress());
    }

    @Override
    public @NotNull ScheduledExecutorService getEventLoop() {
        return this.channel.eventLoop();
    }

    @Override
    public @NotNull String getChannelId() {
        return this.channel.id().asLongText();
    }

    @Override
    public boolean isOpen() {
        return this.channel.isOpen();
    }

    @Override
    public boolean isRegistered() {
        return this.channel.isRegistered();
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
    public @NotNull NetworkAddress getLocalAddress() {
        return this.localAddress;
    }

    @Override
    public @NotNull NetworkAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    @Override
    public void flush() {
        this.channel.flush();
    }

    @Override
    public int compareTo(@NotNull NetworkChannel channel) {
        return this.getChannelId().compareTo(channel.getChannelId());
    }

    @Override
    public @NotNull ChannelListener getListener() {
        return this.channelListener;
    }

    @Override
    public void setListener(@NotNull ChannelListener listener) {
        this.channelListener = listener;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public void setName(@NotNull String newName) {
        this.name = newName;
    }
}
