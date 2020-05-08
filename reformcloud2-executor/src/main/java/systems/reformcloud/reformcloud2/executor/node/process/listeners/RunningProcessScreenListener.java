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
package systems.reformcloud.reformcloud2.executor.node.process.listeners;

import systems.reformcloud.reformcloud2.executor.api.common.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.ProcessScreenEnabledEvent;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.events.ProcessScreenEntryCachedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.screen.NodePacketOutScreenEnabled;
import systems.reformcloud.reformcloud2.executor.node.network.packet.out.screen.NodePacketOutScreenLineAdded;

public class RunningProcessScreenListener {

    @Listener
    public void handle(final ProcessScreenEntryCachedEvent event) {
        for (String receiver : event.getRunningProcessScreen().getReceivers()) {
            if (receiver.equals(NodeExecutor.getInstance().getNodeConfig().getName())) {
                System.out.println(LanguageManager.get(
                        "screen-line-added",
                        event.getRunningProcessScreen().getTargetProcess().getProcessInformation().getProcessDetail().getName(),
                        event.getCachedLogLine()
                ));
                return;
            }

            ReferencedOptional<PacketSender> referencedOptional = DefaultChannelManager.INSTANCE.get(receiver);
            if (referencedOptional.isEmpty()) {
                continue;
            }

            referencedOptional.get().sendPacket(new NodePacketOutScreenLineAdded(
                    event.getRunningProcessScreen().getTargetProcess().getProcessInformation().getProcessDetail().getProcessUniqueID(),
                    event.getCachedLogLine()
            ));
        }
    }

    @Listener
    public void handle(final ProcessScreenEnabledEvent event) {
        if (event.getEnabledFor().equals(NodeExecutor.getInstance().getNodeConfig().getName())) {
            for (String lastLogLine : event.getRunningProcessScreen().getLastLogLines()) {
                System.out.println(LanguageManager.get(
                        "screen-line-added",
                        event.getRunningProcessScreen().getTargetProcess().getProcessInformation().getProcessDetail().getName(),
                        lastLogLine
                ));
            }

            return;
        }

        DefaultChannelManager.INSTANCE
                .get(event.getEnabledFor())
                .ifPresent(sender -> sender.sendPacket(new NodePacketOutScreenEnabled(
                        event.getRunningProcessScreen().getTargetProcess().getProcessInformation().getProcessDetail().getProcessUniqueID(),
                        event.getRunningProcessScreen().getLastLogLines()
                )));
    }
}
