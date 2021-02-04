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
package systems.refomcloud.embedded.plugin.spigot.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.Embedded;
import systems.refomcloud.embedded.plugin.spigot.SpigotExecutor;
import systems.refomcloud.embedded.shared.SharedDisconnectHandler;
import systems.refomcloud.embedded.shared.SharedJoinAllowChecker;
import systems.reformcloud.shared.collect.Entry2;

public final class PlayerListenerHandler implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(final @NotNull PlayerLoginEvent event) {
    if (!Embedded.getInstance().isReady()) {
      event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
      event.setKickMessage(SpigotExecutor.getInstance().getIngameMessages().format(
        SpigotExecutor.getInstance().getIngameMessages().getProcessNotReadyToAcceptPlayersMessage()
      ));
      return;
    }

    Entry2<Boolean, String> checked = SharedJoinAllowChecker.checkIfConnectAllowed(
      event.getPlayer()::hasPermission,
      SpigotExecutor.getInstance().getIngameMessages(),
      null,
      event.getPlayer().getUniqueId(),
      event.getPlayer().getName()
    );
    if (!checked.getFirst() && checked.getSecond() != null) {
      event.setKickMessage(checked.getSecond());
      event.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
    } else {
      event.setResult(PlayerLoginEvent.Result.ALLOWED);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void handle(final @NotNull PlayerQuitEvent event) {
    SharedDisconnectHandler.handleDisconnect(event.getPlayer().getUniqueId());
  }
}
