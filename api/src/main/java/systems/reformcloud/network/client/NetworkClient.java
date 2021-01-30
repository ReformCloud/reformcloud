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
package systems.reformcloud.network.client;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.address.NetworkAddress;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.channel.listener.ChannelListener;

import java.util.function.Function;

/**
 * A client that can connect to a network server.
 */
public interface NetworkClient extends NetworkChannel {
  /**
   * Connects to a network server.
   *
   * @param host                   The host of the network server.
   * @param port                   The port of the network server.
   * @param channelListenerFactory The factory creating the channel listener for the connection.
   * @return {@code true} if the connection was established successfully, else {@code false}.
   */
  boolean connect(@NotNull String host, int port, @NotNull Function<NetworkChannel, ChannelListener> channelListenerFactory);

  /**
   * Connects to a network server.
   *
   * @param address                The address of the network server.
   * @param channelListenerFactory The factory creating the channel listener for the connection.
   * @return {@code true} if the connection was established successfully, else {@code false}.
   */
  boolean connect(@NotNull NetworkAddress address, @NotNull Function<NetworkChannel, ChannelListener> channelListenerFactory);
}
