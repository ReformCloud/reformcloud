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
package systems.reformcloud.reformcloud2.executor.node.process.log;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.screen.NodePacketOutScreenEnabled;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.screen.NodePacketOutScreenLineAdded;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class NodeProcessScreen {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public NodeProcessScreen(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    private final UUID uniqueID;

    private final Deque<String> queue = new ConcurrentLinkedDeque<>();

    private final Collection<String> enabledFrom = new ArrayList<>();

    public boolean toggleFor(String name) {
        if (enabledFrom.contains(name)) {
            enabledFrom.remove(name);
            return false;
        }

        enabledFrom.add(name);
        if (name.equals(NodeExecutor.getInstance().getNodeConfig().getName())) {
            ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(uniqueID);
            if (processInformation != null) {
                queue.forEach(line -> System.out.println(LanguageManager.get("screen-line-added", processInformation.getProcessDetail().getName(), line)));
            }
        } else {
            DefaultChannelManager.INSTANCE.get(name).ifPresent(e -> e.sendPacket(new NodePacketOutScreenEnabled(
                    uniqueID, queue
            )));
        }

        return true;
    }

    void addScreenLine(String line) {
        if (line.equals(LINE_SEPARATOR)) {
            return;
        }

        while (queue.size() >= 128) {
            queue.remove();
        }

        queue.offerLast(line);
        enabledFrom.stream()
                .filter(e -> DefaultChannelManager.INSTANCE.get(e).isPresent())
                .map(e -> DefaultChannelManager.INSTANCE.get(e).orNothing())
                .filter(Objects::nonNull)
                .forEach(e -> e.sendPacket(new NodePacketOutScreenLineAdded(uniqueID, line)));

        ProcessInformation processInformation = ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().getProcess(uniqueID);
        if (processInformation != null && enabledFrom.contains(NodeExecutor.getInstance().getNodeConfig().getName())) {
            System.out.println(LanguageManager.get("screen-line-added", processInformation.getProcessDetail().getName(), line));
        }
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public Deque<String> getCachedLines() {
        return queue;
    }
}
