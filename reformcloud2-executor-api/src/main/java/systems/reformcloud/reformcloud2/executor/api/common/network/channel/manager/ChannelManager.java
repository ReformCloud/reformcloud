/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface ChannelManager {

    /**
     * Registers a network channel
     *
     * @param packetSender The network channel which should get registered
     */
    void registerChannel(@NotNull PacketSender packetSender);

    /**
     * Unregisters a channel
     *
     * @param packetSender The packet sender of the channel which should get unregistered
     */
    void unregisterChannel(@NotNull PacketSender packetSender);

    void broadcast(@NotNull Packet packet);

    void broadcast(@NotNull Packet packet, @NotNull Predicate<PacketSender> packetSenderPredicate);

    /**
     * Unregisters all channels
     */
    void unregisterAll();

    /**
     * @param name The name of the packet sender who should be found
     * @return A {@link ReferencedOptional} which is
     * a) empty if the packet sender is not registered
     * b) contains the packet sender if the channel is registered
     * @see ReferencedOptional#isPresent()
     * @see ReferencedOptional#ifPresent(Consumer)
     */
    @NotNull
    ReferencedOptional<PacketSender> get(@NotNull String name);

    /**
     * @return All registered packet senders
     */
    @NotNull
    List<PacketSender> getAllSender();
}
