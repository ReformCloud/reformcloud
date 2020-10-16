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
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;

import java.net.InetSocketAddress;
import java.util.UUID;

public final class DefaultNetworkChannel implements NetworkChannel {

    private final Channel channel;
    private final long connectionTime = System.currentTimeMillis();
    private InetSocketAddress address;
    private boolean authenticated;
    private String name;
    DefaultNetworkChannel(Channel channel) {
        this.channel = channel;
        this.address = (InetSocketAddress) channel.remoteAddress();
    }

    @Override
    public void sendPacket(@NotNull Object packet) {
        this.channel.writeAndFlush(packet, this.channel.voidPromise());
    }

    @Override
    public void sendPackets(@NonNls Object... packets) {
        for (Object packet : packets) {
            this.sendPacket(packet);
        }
    }

    @Override
    public void sendQueryResult(@Nullable UUID queryUniqueID, @NotNull Packet result) {
        result.setQueryUniqueID(queryUniqueID);
        this.sendPacket(result);
    }

    @Override
    public long getConnectionTime() {
        return this.connectionTime;
    }

    @NotNull
    @Override
    public String getAddress() {
        return this.address.getAddress().getHostAddress();
    }

    @NotNull
    @Override
    public InetSocketAddress getEthernetAddress() {
        return this.address;
    }

    @Override
    public void setRemoteAddress(@NotNull InetSocketAddress address) {
        this.address = address;
    }

    @Override
    public boolean isConnected() {
        return this.channel.isOpen() && this.channel.isWritable();
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public void close() {
        this.channel.close(this.channel.voidPromise());
    }

    @NotNull
    @Override
    public String getName() {
        return this.name == null ? "" : this.name;
    }

    @Override
    public void setName(@NotNull String newName) {
        this.name = newName;
    }
}
