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
package systems.reformcloud.network.channel.listener;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.packet.Packet;

/**
 * A channel listener.
 */
public interface ChannelListener {

  /**
   * Checks if this listener should handle the received packet.
   *
   * @param packet The received packet.
   * @return {@code true} if the packet should be handled, else {@code false}.
   */
  boolean shouldHandle(@NotNull Packet packet);

  /**
   * Called when the {@code channel} is active for the first time.
   *
   * @param channel The channel which is active.
   */
  void channelActive(@NotNull NetworkChannel channel);

  /**
   * Called when the {@code channel} is inactive.
   *
   * @param channel The channel which is inactive.
   */
  void channelInactive(@NotNull NetworkChannel channel);

  /**
   * Called when the write ability of the {@code channel} changes.
   *
   * @param channel The channel of which the write ability changed.
   */
  void channelWriteAbilityChanged(@NotNull NetworkChannel channel);

  /**
   * Handles the received packet.
   *
   * @param input The received packet.
   */
  void handle(@NotNull Packet input);

  /**
   * Called when an exception was caught in the {@code channel}.
   *
   * @param channel The channel in which the exception was thrown.
   * @param cause   The exception which was thrown.
   */
  void exceptionCaught(@NotNull NetworkChannel channel, @NotNull Throwable cause);

  /**
   * Called when the read operation of a channel was completed successfully.
   *
   * @param channel The channel in which the channel read finished.
   */
  void readOperationCompleted(@NotNull NetworkChannel channel);
}
