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
package systems.reformcloud.network.server;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.address.NetworkAddress;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.channel.listener.ChannelListener;

import java.util.function.Function;

/**
 * A network server that can listen on some ports.
 */
public interface NetworkServer extends Server {

  /**
   * Binds a network listener to the given port.
   *
   * @param host                   The host to bind the server to.
   * @param port                   The port to bind the server to.
   * @param channelListenerFactory The factory creating the channel listener.
   * @return {@code true} if the bind operation was successful, else {@code false}.
   */
  boolean bind(@NotNull String host, int port, @NotNull Function<NetworkChannel, ChannelListener> channelListenerFactory);

  /**
   * Binds a network listener to the given port.
   *
   * @param address                The address to bind to.
   * @param channelListenerFactory The factory creating the channel listener.
   * @return {@code true} if the bind operation was successful, else {@code false}.
   */
  boolean bind(@NotNull NetworkAddress address, @NotNull Function<NetworkChannel, ChannelListener> channelListenerFactory);
}