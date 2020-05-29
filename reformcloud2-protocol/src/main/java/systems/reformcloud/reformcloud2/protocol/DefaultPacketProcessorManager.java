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
package systems.reformcloud.reformcloud2.protocol;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.network.packet.Packet;
import systems.reformcloud.reformcloud2.protocol.processor.PacketProcessor;
import systems.reformcloud.reformcloud2.protocol.processor.PacketProcessorManager;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPacketProcessorManager extends PacketProcessorManager {

    static {
        PacketProcessorManager.setInstance(new DefaultPacketProcessorManager());
    }

    private DefaultPacketProcessorManager() {
    }

    private final Map<Class<? extends Packet>, PacketProcessor<? extends Packet>> processors = new ConcurrentHashMap<>();

    @Override
    public @NotNull <T extends Packet> PacketProcessorManager registerProcessor(@NotNull PacketProcessor<T> packetProcessor, @NotNull Class<T> packetClass) {
        this.processors.put(packetClass, packetProcessor);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T extends Packet> Optional<PacketProcessor<T>> getPacketProcessor(@NotNull Class<T> packetClass) {
        PacketProcessor<T> packetProcessor = (PacketProcessor<T>) this.processors.get(packetClass);
        return Optional.ofNullable(packetProcessor);
    }
}
