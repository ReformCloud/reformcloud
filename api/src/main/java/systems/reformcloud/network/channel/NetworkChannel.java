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
package systems.reformcloud.network.channel;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.functional.Sorted;
import systems.reformcloud.network.address.NetworkAddress;
import systems.reformcloud.network.channel.listener.ChannelListenerHolder;
import systems.reformcloud.utility.name.ReNameable;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Represents a network channel.
 */
public interface NetworkChannel extends ChannelListenerHolder, Sorted<NetworkChannel>, PacketSender, ReNameable {
  /**
   * Get the event loop of this channel.
   *
   * @return The event loop of this channel.
   */
  @NotNull
  ScheduledExecutorService getEventLoop();

  /**
   * Get the channel id.
   *
   * @return The channel id.
   */
  @NotNull
  String getChannelId();

  /**
   * Gets if the channel is still open.
   *
   * @return If the channel is still open.
   */
  boolean isOpen();

  /**
   * Gets if the channel is registered.
   *
   * @return If the channel is registered.
   */
  boolean isRegistered();

  /**
   * Gets if this channel is active.
   *
   * @return If this channel is active.
   */
  boolean isActive();

  /**
   * Gets if this channel is writeable.
   *
   * @return If this channel is writeable.
   */
  boolean isWritable();

  /**
   * Get the remote address of the channel (the address the connection is coming from).
   *
   * @return The remote address of the channel.
   */
  @NotNull
  NetworkAddress getLocalAddress();

  /**
   * Get the local address of the channel (the address the connection is connected to).
   *
   * @return The local address of the client.
   */
  @NotNull
  NetworkAddress getRemoteAddress();

  /**
   * Flushes this channel.
   */
  void flush();
}
