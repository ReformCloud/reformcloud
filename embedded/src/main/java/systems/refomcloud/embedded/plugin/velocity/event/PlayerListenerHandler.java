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
package systems.refomcloud.embedded.plugin.velocity.event;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.Embedded;
import systems.refomcloud.embedded.controller.ProxyServerController;
import systems.refomcloud.embedded.plugin.velocity.VelocityExecutor;
import systems.refomcloud.embedded.plugin.velocity.fallback.VelocityFallbackExtraFilter;
import systems.refomcloud.embedded.shared.SharedJoinAllowChecker;
import systems.refomcloud.embedded.shared.SharedPlayerFallbackFilter;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.process.ProcessState;
import systems.reformcloud.shared.collect.Entry2;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public final class PlayerListenerHandler {

  @Subscribe(order = PostOrder.FIRST)
  public void handle(final @NotNull ServerPreConnectEvent event) {
    if (!event.getPlayer().getCurrentServer().isPresent()) {
      final Optional<ProcessInformation> fallback = SharedPlayerFallbackFilter.filterFallback(
        event.getPlayer().getUniqueId(),
        this.getServerController().getCachedLobbyServers(),
        event.getPlayer()::hasPermission,
        VelocityFallbackExtraFilter.INSTANCE,
        null
      );
      if (fallback.isPresent()) {
        Optional<RegisteredServer> server = VelocityExecutor.getInstance().getProxyServer().getServer(fallback.get().getName());
        if (!server.isPresent()) {
          event.getPlayer().disconnect(VelocityExecutor.SERIALIZER.deserialize(VelocityExecutor.getInstance().getIngameMessages().format(
            VelocityExecutor.getInstance().getIngameMessages().getNoHubServerAvailable()
          )));
        } else {
          event.setResult(ServerPreConnectEvent.ServerResult.allowed(server.get()));
        }
      } else {
        event.getPlayer().disconnect(VelocityExecutor.SERIALIZER.deserialize(VelocityExecutor.getInstance().getIngameMessages().format(
          VelocityExecutor.getInstance().getIngameMessages().getNoHubServerAvailable()
        )));
      }
    }
  }

  @Subscribe(order = PostOrder.FIRST)
  public void handle(final @NotNull LoginEvent event) {
    if (!Embedded.getInstance().isReady()) {
      event.setResult(ResultedEvent.ComponentResult.denied(VelocityExecutor.SERIALIZER.deserialize(VelocityExecutor.getInstance().getIngameMessages().format(
        VelocityExecutor.getInstance().getIngameMessages().getProcessNotReadyToAcceptPlayersMessage()
      ))));
      return;
    }

    Entry2<Boolean, String> checked = SharedJoinAllowChecker.checkIfConnectAllowed(
      event.getPlayer()::hasPermission,
      VelocityExecutor.getInstance().getIngameMessages(),
      this.getServerController().getCachedProxies(),
      event.getPlayer().getUniqueId(),
      event.getPlayer().getUsername()
    );
    if (!checked.getFirst() && checked.getSecond() != null) {
      event.setResult(ResultedEvent.ComponentResult.denied(VelocityExecutor.SERIALIZER.deserialize(checked.getSecond())));
    }
  }

  @Subscribe(order = PostOrder.FIRST)
  public void handle(final @NotNull KickedFromServerEvent event) {
    final Optional<ProcessInformation> fallback = SharedPlayerFallbackFilter.filterFallback(
      event.getPlayer().getUniqueId(),
      this.getServerController().getCachedLobbyServers(),
      event.getPlayer()::hasPermission,
      VelocityFallbackExtraFilter.INSTANCE,
      event.getPlayer().getCurrentServer().map(e -> e.getServerInfo().getName()).orElse(null)
    );
    if (fallback.isPresent()) {
      Optional<RegisteredServer> server = VelocityExecutor.getInstance().getProxyServer().getServer(fallback.get().getName());
      if (!server.isPresent()) {
        event.setResult(KickedFromServerEvent.DisconnectPlayer.create(VelocityExecutor.SERIALIZER.deserialize(
          VelocityExecutor.getInstance().getIngameMessages().format(VelocityExecutor.getInstance().getIngameMessages().getNoHubServerAvailable())
        )));
      } else {
        event.setResult(KickedFromServerEvent.RedirectPlayer.create(server.get()));
        event.getServerKickReason().ifPresent(reason -> event.getPlayer().sendMessage(Identity.nil(), reason));
      }
    } else {
      event.setResult(KickedFromServerEvent.DisconnectPlayer.create(VelocityExecutor.SERIALIZER.deserialize(
        VelocityExecutor.getInstance().getIngameMessages().format(VelocityExecutor.getInstance().getIngameMessages().getNoHubServerAvailable())
      )));
    }
  }

  @Subscribe(order = PostOrder.FIRST)
  public void handle(final DisconnectEvent event) {
    VelocityExecutor.getInstance().getProxyServer().getScheduler().buildTask(VelocityExecutor.getInstance().getPlugin(), () -> {
      ProcessInformation current = Embedded.getInstance().getCurrentProcessInformation();
      if (VelocityExecutor.getInstance().getProxyServer().getPlayerCount() < Embedded.getInstance().getMaxPlayers()
        && !current.getCurrentState().equals(ProcessState.READY)
        && !current.getCurrentState().equals(ProcessState.INVISIBLE)) {
        current.setCurrentState(ProcessState.READY);
      }

      current.getPlayers().removeIf(player -> player.getUniqueID().equals(event.getPlayer().getUniqueId()));
      Embedded.getInstance().updateCurrentProcessInformation();
    }).delay(20, TimeUnit.MILLISECONDS).schedule();
  }

  private @NotNull ProxyServerController getServerController() {
    return ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProxyServerController.class);
  }
}
