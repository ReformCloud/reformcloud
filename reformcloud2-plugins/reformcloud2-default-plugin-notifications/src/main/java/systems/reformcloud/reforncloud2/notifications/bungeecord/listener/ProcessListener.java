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
package systems.reformcloud.reforncloud2.notifications.bungeecord.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.event.events.process.ProcessRegisterEvent;
import systems.reformcloud.reformcloud2.executor.api.event.events.process.ProcessUnregisterEvent;
import systems.reformcloud.reformcloud2.executor.api.event.events.process.ProcessUpdateEvent;
import systems.reformcloud.reformcloud2.executor.api.event.handler.Listener;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ProcessListener {

    private final Map<UUID, ProcessInformation> registered = new ConcurrentHashMap<>();

    public ProcessListener() {
        for (ProcessInformation process : ExecutorAPI.getInstance().getProcessProvider().getProcesses()) {
            this.registered.put(process.getProcessDetail().getProcessUniqueID(), process);
        }
    }

    @Listener
    public void handle(final ProcessRegisterEvent event) {
        this.publishNotification(
                Embedded.getInstance().getIngameMessages().getProcessStarted(),
                event.getProcessInformation().getProcessDetail().getName()
        );
    }

    @Listener
    public void handle(final ProcessUnregisterEvent event) {
        if (!this.registered.containsKey(event.getProcessInformation().getProcessDetail().getProcessUniqueID())) {
            return;
        }

        this.publishNotification(
                Embedded.getInstance().getIngameMessages().getProcessStopped(),
                event.getProcessInformation().getProcessDetail().getName()
        );
        this.registered.remove(event.getProcessInformation().getProcessDetail().getProcessUniqueID());
    }

    @Listener
    public void handle(final ProcessUpdateEvent event) {
        ProcessInformation old = this.registered.put(
                event.getProcessInformation().getProcessDetail().getProcessUniqueID(),
                event.getProcessInformation()
        );
        if (old != null) {
            if (!old.getNetworkInfo().isConnected() && event.getProcessInformation().getNetworkInfo().isConnected()) {
                this.publishNotification(
                        Embedded.getInstance().getIngameMessages().getProcessConnected(),
                        event.getProcessInformation().getProcessDetail().getName()
                );
            }

            return;
        }

        this.publishNotification(
                Embedded.getInstance().getIngameMessages().getProcessRegistered(),
                event.getProcessInformation().getProcessDetail().getName()
        );
    }

    private void publishNotification(String message, Object... replacements) {
        String replacedMessage = Embedded.getInstance().getIngameMessages().format(message, replacements);
        ProxyServer.getInstance().getPlayers()
                .stream()
                .filter(e -> e.hasPermission("reformcloud.notify"))
                .forEach(player -> player.sendMessage(TextComponent.fromLegacyText(replacedMessage)));
    }
}
