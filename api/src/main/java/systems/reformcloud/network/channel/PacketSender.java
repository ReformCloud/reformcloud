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
import systems.reformcloud.network.packet.Packet;
import systems.reformcloud.utility.name.Nameable;

import java.util.UUID;
import java.util.concurrent.Future;

/**
 * A sender of packets.
 */
public interface PacketSender extends Nameable {
  /**
   * Gets if the channel is connected and authenticated.
   *
   * @return If the channel is connected and authenticated.
   */
  boolean isKnown();

  /**
   * Sends a packet to this packet sender.
   *
   * @param packet The packet to send.
   * @return A future completed when the packet was sent.
   */
  @NotNull
  Future<Void> sendPacket(@NotNull Packet packet);

  /**
   * Sends a packet to this sender waiting for the operation to complete.
   *
   * @param packet The packet to send.
   */
  void sendPacketSync(@NotNull Packet packet);

  /**
   * Sends many packets to this packet sender.
   *
   * @param packets The packets to send.
   */
  void sendPackets(@NotNull Packet... packets);

  /**
   * Sends a response for a query to this sender.
   *
   * @param queryUniqueId The unique id of the query packet received.
   * @param result        The response packet.
   */
  void sendQueryResult(@NotNull UUID queryUniqueId, @NotNull Packet result);

  /**
   * Closes the connection to the packet sender.
   *
   * @return A future completed when the close action was completed.
   */
  @NotNull
  Future<Void> close();

  /**
   * Closes the connection to the packet sender waiting for the operation to complete.
   */
  void closeSync();
}
