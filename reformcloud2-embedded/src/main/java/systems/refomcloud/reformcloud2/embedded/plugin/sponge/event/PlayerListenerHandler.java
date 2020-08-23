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
package systems.refomcloud.reformcloud2.embedded.plugin.sponge.event;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.refomcloud.reformcloud2.embedded.plugin.sponge.SpongeExecutor;
import systems.refomcloud.reformcloud2.embedded.shared.SharedJoinAllowChecker;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Duo;

import java.util.concurrent.TimeUnit;

public final class PlayerListenerHandler {

    @Listener(order = Order.LATE)
    public void handle(final @NotNull ClientConnectionEvent.Login event) {
        if (!Embedded.getInstance().isReady()) {
            event.setCancelled(true);
            event.setMessage(Text.of(SpongeExecutor.getInstance().getIngameMessages().format(
                    SpongeExecutor.getInstance().getIngameMessages().getProcessNotReadyToAcceptPlayersMessage()
            )));
            return;
        }

        Duo<Boolean, String> checked = SharedJoinAllowChecker.checkIfConnectAllowed(
                event.getTargetUser()::hasPermission,
                SpongeExecutor.getInstance().getIngameMessages(),
                null,
                event.getTargetUser().getUniqueId(),
                event.getTargetUser().getName()
        );
        if (!checked.getFirst() && checked.getSecond() != null) {
            event.setCancelled(true);
            event.setMessage(Text.of(checked.getSecond()));
        }
    }

    @Listener(order = Order.FIRST)
    public void handle(final @NotNull ClientConnectionEvent.Disconnect event) {
        ProcessInformation current = Embedded.getInstance().getCurrentProcessInformation();
        if (!current.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(event.getTargetEntity().getUniqueId())) {
            return;
        }

        SpongeExecutor.getInstance().getExecutorService().schedule(() -> {
            if (Sponge.getServer().getOnlinePlayers().size() < current.getProcessDetail().getMaxPlayers()
                    && !current.getProcessDetail().getProcessState().equals(ProcessState.READY)
                    && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
                current.getProcessDetail().setProcessState(ProcessState.READY);
            }

            current.updateRuntimeInformation();
            current.getProcessPlayerManager().onLogout(event.getTargetEntity().getUniqueId());

            Embedded.getInstance().updateCurrentProcessInformation();
        }, 20, TimeUnit.MILLISECONDS);
    }
}
