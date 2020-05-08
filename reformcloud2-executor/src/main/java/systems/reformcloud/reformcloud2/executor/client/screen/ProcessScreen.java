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
package systems.reformcloud.reformcloud2.executor.client.screen;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.controller.network.packet.ClientPacketAddScreenLine;
import systems.reformcloud.reformcloud2.executor.controller.network.packet.ClientPacketScreenEnabled;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ProcessScreen {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public ProcessScreen(UUID uuid) {
        this.uuid = uuid;
    }

    private final UUID uuid;

    private final Queue<String> queue = new ConcurrentLinkedQueue<>();

    private boolean enabled = false;

    void addScreenLine(String line) {
        if (line.equals(LINE_SEPARATOR)) {
            return;
        }

        while (queue.size() >= 128) {
            queue.poll();
        }

        queue.add(line);
        if (enabled) {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new ClientPacketAddScreenLine(uuid, line)));
        }
    }

    public void toggleScreen() {
        this.enabled = !this.enabled;

        if (this.enabled) {
            DefaultChannelManager.INSTANCE.get("Controller").ifPresent(packetSender -> packetSender.sendPacket(new ClientPacketScreenEnabled(uuid, queue)));
        }
    }

    public Queue<String> getQueue() {
        return queue;
    }
}
