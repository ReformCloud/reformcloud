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
package systems.reformcloud.network.packet.query;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.channel.NetworkChannel;
import systems.reformcloud.network.packet.Packet;
import systems.reformcloud.task.Task;

import java.util.Optional;
import java.util.UUID;

/**
 * The query manager handling the network query operations.
 */
public interface QueryManager {

  /**
   * Gets the waiting query task associated with the given {@code queryUniqueId}.
   *
   * @param queryUniqueId The query unique id of the packet task to get.
   * @return The task associated with the given {@code queryUniqueId}.
   */
  @NotNull
  Optional<Task<Packet>> getWaitingQuery(@NotNull UUID queryUniqueId);

  /**
   * Sends a query into a the given {@code channel}.
   *
   * @param channel The channel to send the query to.
   * @param packet  The packet which should be sent as a query packet.
   * @return The task waiting for the operation to complete.
   */
  @NotNull
  default Task<Packet> sendPacketQuery(@NotNull NetworkChannel channel, @NotNull Packet packet) {
    return this.sendPacketQuery(channel, UUID.randomUUID(), packet);
  }

  /**
   * Sends a query into a the given {@code channel}.
   *
   * @param channel       The channel to send the query to.
   * @param queryUniqueId The unique id of the query to send.
   * @param packet        The packet which should be sent as a query packet.
   * @return The task waiting for the operation to complete.
   */
  @NotNull
  Task<Packet> sendPacketQuery(@NotNull NetworkChannel channel, @NotNull UUID queryUniqueId, @NotNull Packet packet);
}
