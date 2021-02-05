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
package systems.reformcloud.commands.plugin.waterdog.commands;

import dev.waterdog.ProxyServer;
import dev.waterdog.command.Command;
import dev.waterdog.command.CommandSender;
import dev.waterdog.command.CommandSettings;
import dev.waterdog.network.ServerInfo;
import dev.waterdog.player.ProxiedPlayer;
import systems.refomcloud.embedded.Embedded;
import systems.refomcloud.embedded.controller.ProxyServerController;
import systems.refomcloud.embedded.plugin.bungee.fallback.BungeeFallbackExtraFilter;
import systems.refomcloud.embedded.shared.SharedPlayerFallbackFilter;
import systems.reformcloud.ExecutorAPI;
import systems.reformcloud.process.ProcessInformation;

import java.util.Optional;

public class CommandLeave extends Command {

  public CommandLeave(String name, String[] aliases) {
    super(name, CommandSettings.builder().setAliases(aliases).build());
  }

  @Override
  public boolean onExecute(CommandSender commandSender, String s, String[] strings) {
    if (!(commandSender instanceof ProxiedPlayer)) {
      return false;
    }

    final ProxiedPlayer player = (ProxiedPlayer) commandSender;
    if (player.getServer() == null) {
      return true;
    }

    if (ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProxyServerController.class).getCachedLobbyServers().stream().anyMatch(
      e -> e.getName().equals(player.getServer().getInfo().getServerName()))
    ) {
      player.sendMessage(Embedded.getInstance().getIngameMessages().format(Embedded.getInstance().getIngameMessages().getAlreadyConnectedToHub()));
      return true;
    }

    final Optional<ProcessInformation> fallback = SharedPlayerFallbackFilter.filterFallback(
      player.getUniqueId(),
      ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(ProxyServerController.class).getCachedLobbyServers(),
      player::hasPermission,
      BungeeFallbackExtraFilter.INSTANCE,
      player.getServer().getInfo().getServerName()
    );
    if (fallback.isPresent()) {
      final ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(fallback.get().getName());
      if (serverInfo == null) {
        player.sendMessage(Embedded.getInstance().getIngameMessages().format(Embedded.getInstance().getIngameMessages().getNoHubServerAvailable()));
        return true;
      }

      player.sendMessage(Embedded.getInstance().getIngameMessages().format(Embedded.getInstance().getIngameMessages().getConnectingToHub(), fallback.get().getName()));
      player.connect(serverInfo);
    } else {
      player.sendMessage(Embedded.getInstance().getIngameMessages().format(Embedded.getInstance().getIngameMessages().getNoHubServerAvailable()));
    }
    return true;
  }
}
