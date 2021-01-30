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
package systems.reformcloud.network.packet;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.network.packet.exception.PacketAlreadyRegisteredException;

import java.util.Collection;
import java.util.Optional;

/**
 * A packet provider.
 */
public interface PacketProvider {

  /**
   * Registers a packet ignoring the {@link PacketAlreadyRegisteredException} if the packet is already registered.
   *
   * @param packetClass The class of the packet.
   */
  default void registerPacketIgnored(@NotNull Class<? extends Packet> packetClass) {
    try {
      this.registerPacket(packetClass);
    } catch (PacketAlreadyRegisteredException ignored) {
    }
  }

  /**
   * Registers a packet to this provider.
   *
   * @param packetClass The class of the packet.
   * @throws PacketAlreadyRegisteredException when the packet is already registered.
   */
  void registerPacket(@NotNull Class<? extends Packet> packetClass) throws PacketAlreadyRegisteredException;

  /**
   * Registers a packet ignoring the {@link PacketAlreadyRegisteredException} if the packet is already registered.
   *
   * @param packet The packet which should get registered.
   */
  default void registerPacketIgnored(@NotNull Packet packet) {
    try {
      this.registerPacket(packet);
    } catch (PacketAlreadyRegisteredException ignored) {
    }
  }

  /**
   * Registers a packet to this provider.
   *
   * @param packet The packet which should get registered.
   * @throws PacketAlreadyRegisteredException when the packet is already registered.
   */
  void registerPacket(@NotNull Packet packet) throws PacketAlreadyRegisteredException;

  /**
   * Registers the packets to this provider.
   *
   * @param packetClasses the packet classes which should get registered to this provider.
   * @throws PacketAlreadyRegisteredException when one of the packets is already registered.
   */
  void registerPackets(@NotNull Collection<Class<? extends Packet>> packetClasses) throws PacketAlreadyRegisteredException;

  /**
   * Registers the packets to this provider.
   *
   * @param packets the packets which should get registered to this provider.
   * @throws PacketAlreadyRegisteredException when one of the packets is already registered.
   */
  void registerPacket(@NotNull Collection<Packet> packets) throws PacketAlreadyRegisteredException;

  /**
   * Unregisters the packet by the given id.
   *
   * @param id the id of the packet to unregister.
   */
  void unregisterPacket(int id);

  /**
   * Gets a specific packet by the packet id.
   *
   * @param id The id of the packet to get.
   * @return The packet.
   */
  @NotNull
  Optional<Packet> getPacketById(int id);

  /**
   * Clears the packets known to this provider.
   */
  void clearRegisteredPackets();
}
