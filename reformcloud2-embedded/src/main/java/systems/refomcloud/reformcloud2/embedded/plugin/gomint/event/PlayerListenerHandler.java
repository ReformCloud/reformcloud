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
package systems.refomcloud.reformcloud2.embedded.plugin.gomint.event;

import io.gomint.GoMint;
import io.gomint.event.EventHandler;
import io.gomint.event.EventListener;
import io.gomint.event.player.PlayerLoginEvent;
import io.gomint.event.player.PlayerQuitEvent;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.refomcloud.reformcloud2.embedded.plugin.gomint.GoMintExecutor;
import systems.refomcloud.reformcloud2.embedded.shared.SharedJoinAllowChecker;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.utility.list.Duo;

public class PlayerListenerHandler implements EventListener {

    @EventHandler
    public void handle(PlayerLoginEvent event) {
        if (!Embedded.getInstance().isReady()) {
            event.setCancelled(true);
            event.setKickMessage(GoMintExecutor.getInstance().getIngameMessages().format(
                GoMintExecutor.getInstance().getIngameMessages().getProcessNotReadyToAcceptPlayersMessage()
            ));
            return;
        }

        Duo<Boolean, String> checked = SharedJoinAllowChecker.checkIfConnectAllowed(
            event.getPlayer().getPermissionManager()::hasPermission,
            GoMintExecutor.getInstance().getIngameMessages(),
            null,
            event.getPlayer().getUUID(),
            event.getPlayer().getName()
        );
        if (!checked.getFirst() && checked.getSecond() != null) {
            event.setCancelled(true);
            event.setKickMessage(checked.getSecond());
        }
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        ProcessInformation current = Embedded.getInstance().getCurrentProcessInformation();
        if (!current.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(event.getPlayer().getUUID())) {
            return;
        }

        if (GoMint.instance().getPlayers().size() < current.getProcessDetail().getMaxPlayers()
            && !current.getProcessDetail().getProcessState().equals(ProcessState.READY)
            && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
            current.getProcessDetail().setProcessState(ProcessState.READY);
        }

        current.updateRuntimeInformation();
        current.getProcessPlayerManager().onLogout(event.getPlayer().getUUID());
        Embedded.getInstance().updateCurrentProcessInformation();
    }
}
