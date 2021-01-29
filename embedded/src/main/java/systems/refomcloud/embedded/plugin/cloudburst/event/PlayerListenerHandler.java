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
package systems.refomcloud.embedded.plugin.cloudburst.event;

import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.event.EventPriority;
import org.cloudburstmc.server.event.Listener;
import org.cloudburstmc.server.event.player.PlayerLoginEvent;
import org.cloudburstmc.server.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.Embedded;
import systems.refomcloud.embedded.plugin.cloudburst.CloudBurstExecutor;
import systems.refomcloud.embedded.shared.SharedJoinAllowChecker;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.process.ProcessState;
import systems.reformcloud.shared.collect.Entry2;

public final class PlayerListenerHandler {

  @Listener(priority = EventPriority.HIGH)
  public void handle(final @NotNull PlayerLoginEvent event) {
    if (!Embedded.getInstance().isReady()) {
      event.setCancelled(true);
      event.setKickMessage(CloudBurstExecutor.getInstance().getIngameMessages().format(
        CloudBurstExecutor.getInstance().getIngameMessages().getProcessNotReadyToAcceptPlayersMessage()
      ));
      return;
    }

    Entry2<Boolean, String> checked = SharedJoinAllowChecker.checkIfConnectAllowed(
      event.getPlayer()::hasPermission,
      CloudBurstExecutor.getInstance().getIngameMessages(),
      null,
      event.getPlayer().getServerId(),
      event.getPlayer().getName()
    );
    if (!checked.getFirst() && checked.getSecond() != null) {
      event.setCancelled(true);
      event.setKickMessage(checked.getSecond());
    }
  }

  @Listener(priority = EventPriority.LOWEST)
  public void handle(final @NotNull PlayerQuitEvent event) {
    ProcessInformation current = Embedded.getInstance().getCurrentProcessInformation();
    if (!current.getPlayerByUniqueId(event.getPlayer().getServerId()).isPresent()) {
      return;
    }

    Server.getInstance().getScheduler().scheduleTask(CloudBurstExecutor.getInstance().getPlugin(), () -> {
      if (Server.getInstance().getOnlinePlayers().size() < Embedded.getInstance().getMaxPlayers()
        && !current.getCurrentState().equals(ProcessState.READY)
        && !current.getCurrentState().equals(ProcessState.INVISIBLE)) {
        current.setCurrentState(ProcessState.READY);
      }

      current.getPlayers().removeIf(player -> player.getUniqueID().equals(event.getPlayer().getServerId()));
      Embedded.getInstance().updateCurrentProcessInformation();
    });
  }
}
