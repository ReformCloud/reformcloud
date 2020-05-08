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

import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannel;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.DefaultProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public final class DefaultChannelManager implements ChannelManager {

    public static final ChannelManager INSTANCE = new DefaultChannelManager();

    private final List<PacketSender> senders = new CopyOnWriteArrayList<>();

    @Override
    public void registerChannel(@NotNull PacketSender packetSender) {
        senders.add(packetSender);
    }

    @Override
    public void unregisterChannel(@NotNull PacketSender packetSender) {
        PacketSender current = Streams.filter(senders, sender -> packetSender.getName().equals(sender.getName()));
        if (current == null) {
            return;
        }

        senders.remove(current);
    }

    @Override
    public void broadcast(@NotNull Packet packet) {
        this.broadcast(packet, sender -> true);
    }

    @Override
    public void broadcast(@NotNull Packet packet, @NotNull Predicate<PacketSender> packetSenderPredicate) {
        ProtocolBuffer protocolBuffer = new DefaultProtocolBuffer(Unpooled.buffer());
        packet.write(protocolBuffer);

        for (PacketSender sender : this.senders) {
            sender.sendPacket(protocolBuffer);
        }
    }

    @Override
    public void unregisterAll() {
        senders.forEach(NetworkChannel::close);
        senders.clear();
    }

    @NotNull
    @Override
    public ReferencedOptional<PacketSender> get(@NotNull String name) {
        return Streams.filterToReference(senders, packetSender -> packetSender.getName().equals(name));
    }

    @NotNull
    @Override
    public List<PacketSender> getAllSender() {
        return Collections.unmodifiableList(senders);
    }
}
