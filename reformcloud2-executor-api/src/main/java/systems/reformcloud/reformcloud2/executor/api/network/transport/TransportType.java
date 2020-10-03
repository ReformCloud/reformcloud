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
package systems.reformcloud.reformcloud2.executor.api.network.transport;

import io.netty.bootstrap.ChannelFactory;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.netty.concurrent.FastNettyThreadFactory;

import java.util.concurrent.ThreadFactory;
import java.util.function.BiFunction;

@SuppressWarnings("deprecation") // 1.8 is too old to use the new channel factory
public enum TransportType {

    EPOLL("Epoll", Epoll.isAvailable(), EpollServerSocketChannel::new,
        EpollSocketChannel::new, (type, typeName) -> new EpollEventLoopGroup(Math.min(4, Runtime.getRuntime().availableProcessors() * 2), newThreadFactory(typeName, type))),
    NIO("Nio", true, NioServerSocketChannel::new,
        NioSocketChannel::new, (type, typeName) -> new NioEventLoopGroup(Math.min(4, Runtime.getRuntime().availableProcessors() * 2), newThreadFactory(typeName, type)));

    private final String name;
    private final boolean available;
    private final ChannelFactory<? extends ServerSocketChannel> serverSocketChannelFactory;
    private final ChannelFactory<? extends SocketChannel> socketChannelFactory;
    private final BiFunction<EventLoopGroupType, String, EventLoopGroup> eventLoopGroupFactory;

    TransportType(String name, boolean available, ChannelFactory<? extends ServerSocketChannel> serverSocketChannelFactory,
                  ChannelFactory<? extends SocketChannel> socketChannelFactory, BiFunction<EventLoopGroupType, String, EventLoopGroup> eventLoopGroupFactory) {
        this.name = name;
        this.available = available;
        this.serverSocketChannelFactory = serverSocketChannelFactory;
        this.socketChannelFactory = socketChannelFactory;
        this.eventLoopGroupFactory = eventLoopGroupFactory;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public @NotNull ChannelFactory<? extends ServerSocketChannel> getServerSocketChannelFactory() {
        return this.serverSocketChannelFactory;
    }

    public @NotNull ChannelFactory<? extends SocketChannel> getSocketChannelFactory() {
        return this.socketChannelFactory;
    }

    public @NotNull EventLoopGroup getEventLoopGroup(@NotNull EventLoopGroupType type) {
        return this.eventLoopGroupFactory.apply(type, this.getName());
    }

    /**
     * Get the best transport type for the current machine.
     * For internal use only. Use {@link systems.reformcloud.reformcloud2.executor.api.network.NetworkUtil#TRANSPORT_TYPE}
     *
     * @return the best transport type for the current machine
     */
    @ApiStatus.Internal
    public static @NotNull TransportType getBestType() {
        if (Boolean.getBoolean("reformcloud.disable.native")) {
            return NIO;
        }

        for (TransportType value : TransportType.values()) {
            if (value.isAvailable()) {
                return value;
            }
        }

        return NIO;
    }

    public static @NotNull ThreadFactory newThreadFactory(@NotNull String name, @NotNull EventLoopGroupType type) {
        return new FastNettyThreadFactory("Netty " + type.getName() + ' ' + name + " Thread#%d");
    }
}
