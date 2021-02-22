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
package systems.refomcloud.embedded.plugin.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.Embedded;
import systems.refomcloud.embedded.controller.ProcessEventHandler;
import systems.refomcloud.embedded.controller.ProxyServerController;
import systems.refomcloud.embedded.executor.PlayerAPIExecutor;
import systems.refomcloud.embedded.plugin.bungee.controller.BungeeProxyServerController;
import systems.refomcloud.embedded.plugin.bungee.event.PlayerListenerHandler;
import systems.refomcloud.embedded.plugin.bungee.executor.BungeePlayerAPIExecutor;
import systems.refomcloud.embedded.plugin.bungee.reconnect.ReformCloudReconnectHandler;
import systems.refomcloud.embedded.shared.SharedInvalidPlayerFixer;
import systems.reformcloud.ExecutorType;
import systems.reformcloud.event.EventManager;
import systems.reformcloud.process.ProcessInformation;
import systems.reformcloud.shared.process.DefaultPlayer;

public final class BungeeExecutor extends Embedded {

  private static BungeeExecutor instance;
  private final Plugin plugin;

  BungeeExecutor(Plugin plugin) {
    super.type = ExecutorType.API;

    instance = this;
    this.plugin = plugin;

    PlayerAPIExecutor.setInstance(new BungeePlayerAPIExecutor(plugin));
    ProxyServer.getInstance().setReconnectHandler(new ReformCloudReconnectHandler());

    super.getServiceRegistry().setProvider(ProxyServerController.class, new BungeeProxyServerController(), true);
    super.getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new ProcessEventHandler());

    ProxyServer.getInstance().getPluginManager().registerListener(this.plugin, new PlayerListenerHandler());
    this.fixInvalidPlayers();
  }

  @NotNull
  public static BungeeExecutor getInstance() {
    return instance;
  }

  @Override
  public int getPlayerCount() {
    return ProxyServer.getInstance().getOnlineCount();
  }

  private void fixInvalidPlayers() {
    SharedInvalidPlayerFixer.start(
      uuid -> ProxyServer.getInstance().getPlayer(uuid) != null,
      () -> ProxyServer.getInstance().getOnlineCount()
    );
  }

  @Override
  protected int getMaxPlayersOfEnvironment() {
    return ProxyServer.getInstance().getConfig().getPlayerLimit();
  }

  @Override
  protected void updatePlayersOfEnvironment(@NotNull ProcessInformation information) {
    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
      if (!information.getPlayerByUniqueId(player.getUniqueId()).isPresent()) {
        information.getPlayers().add(new DefaultPlayer(player.getUniqueId(), player.getName(), System.currentTimeMillis()));
      }
    }
  }

  @NotNull
  public Plugin getPlugin() {
    return this.plugin;
  }
}