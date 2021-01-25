/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
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
package systems.refomcloud.reformcloud2.embedded.plugin.bungee.event;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.Embedded;
import systems.refomcloud.reformcloud2.embedded.controller.ProxyServerController;
import systems.refomcloud.reformcloud2.embedded.plugin.bungee.BungeeExecutor;
import systems.refomcloud.reformcloud2.embedded.plugin.bungee.fallback.BungeeFallbackExtraFilter;
import systems.refomcloud.reformcloud2.embedded.plugin.bungee.util.EmptyProxiedPlayer;
import systems.refomcloud.reformcloud2.embedded.shared.SharedJoinAllowChecker;
import systems.refomcloud.reformcloud2.embedded.shared.SharedPlayerFallbackFilter;
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessState;
import systems.reformcloud.reformcloud2.shared.collect.Entry2;

import java.util.Optional;

public final class PlayerListenerHandler implements Listener {

  @EventHandler
  public void handle(final @NotNull ServerConnectEvent event) {
    event.getPlayer().setReconnectServer(null);
    if (event.getPlayer().getServer() == null) {
      final Optional<ProcessInformation> fallback = SharedPlayerFallbackFilter.filterFallback(
        event.getPlayer().getUniqueId(),
        this.getServerController().getCachedLobbyServers(),
        event.getPlayer()::hasPermission,
        BungeeFallbackExtraFilter.INSTANCE,
        null
      );
      if (fallback.isPresent()) {
        event.setTarget(ProxyServer.getInstance().getServerInfo(fallback.get().getName()));
      } else {
        event.getPlayer().disconnect(TextComponent.fromLegacyText(BungeeExecutor.getInstance().getIngameMessages().format(
          Embedded.getInstance().getIngameMessages().getNoHubServerAvailable()
        )));
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(final LoginEvent event) {
    if (!Embedded.getInstance().isReady()) {
      event.setCancelReason(TextComponent.fromLegacyText(BungeeExecutor.getInstance().getIngameMessages().format(
        BungeeExecutor.getInstance().getIngameMessages().getProcessNotReadyToAcceptPlayersMessage()
      )));
      event.setCancelled(true);
      return;
    }

    Entry2<Boolean, String> checked = SharedJoinAllowChecker.checkIfConnectAllowed(
      perm -> new EmptyProxiedPlayer(event.getConnection()).hasPermission(perm),
      BungeeExecutor.getInstance().getIngameMessages(),
      this.getServerController().getCachedProxies(),
      event.getConnection().getUniqueId(),
      event.getConnection().getName()
    );
    if (!checked.getFirst() && checked.getSecond() != null) {
      event.setCancelReason(TextComponent.fromLegacyText(checked.getSecond()));
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void handle(final @NotNull ServerKickEvent event) {
    final Optional<ProcessInformation> fallback = SharedPlayerFallbackFilter.filterFallback(
      event.getPlayer().getUniqueId(),
      this.getServerController().getCachedLobbyServers(),
      event.getPlayer()::hasPermission,
      BungeeFallbackExtraFilter.INSTANCE,
      event.getKickedFrom() == null ? null : event.getKickedFrom().getName()
    );
    if (fallback.isPresent()) {
      ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(fallback.get().getName());
      if (serverInfo != null) {
        event.setCancelServer(serverInfo);
        event.setCancelled(true);
        return;
      }
    }
    event.getPlayer().disconnect(TextComponent.fromLegacyText(BungeeExecutor.getInstance().getIngameMessages().format(
      BungeeExecutor.getInstance().getIngameMessages().getNoHubServerAvailable()
    )));
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void handle(final PlayerDisconnectEvent event) {
    final ProcessInformation current = Embedded.getInstance().getCurrentProcessInformation();

    if (ProxyServer.getInstance().getOnlineCount() < Embedded.getInstance().getMaxPlayers()
      && !current.getCurrentState().equals(ProcessState.READY)
      && !current.getCurrentState().equals(ProcessState.INVISIBLE)) {
      current.setCurrentState(ProcessState.READY);
    }

    current.getPlayers().removeIf(player -> player.getUniqueID().equals(event.getPlayer().getUniqueId()));
    Embedded.getInstance().updateCurrentProcessInformation();
  }

  private @NotNull ProxyServerController getServerController() {
    return ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProxyServerController.class);
  }
}
