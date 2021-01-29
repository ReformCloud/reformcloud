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
package systems.refomcloud.embedded.plugin.bungee.executor;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.executor.PlayerAPIExecutor;

import java.util.UUID;

public class BungeePlayerAPIExecutor extends PlayerAPIExecutor {

  private final BungeeAudiences bungeeAudiences;

  public BungeePlayerAPIExecutor(@NotNull Plugin plugin) {
    this.bungeeAudiences = BungeeAudiences.create(plugin);
  }

  @Override
  public void executeSendMessage(@NotNull UUID player, @NotNull Component message) {
    this.bungeeAudiences.player(player).sendMessage(message);
  }

  @Override
  public void executeKickPlayer(@NotNull UUID player, @NotNull Component message) {
    final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
    if (proxiedPlayer != null) {
      proxiedPlayer.disconnect(BungeeComponentSerializer.get().serialize(message));
    }
  }

  @Override
  public void executePlaySound(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
  }

  @Override
  public void executeSendTitle(@NotNull UUID player, @NotNull Title title) {
    this.bungeeAudiences.player(player).showTitle(title);
  }

  @Override
  public void executeSendActionBar(@NotNull UUID player, @NotNull Component actionBar) {
    this.bungeeAudiences.player(player).sendActionBar(actionBar);
  }

  @Override
  public void executeSendBossBar(@NotNull UUID player, @NotNull BossBar bossBar) {
    this.bungeeAudiences.player(player).showBossBar(bossBar);
  }

  @Override
  public void executeHideBossBar(@NotNull UUID player, @NotNull BossBar bossBar) {
    this.bungeeAudiences.player(player).hideBossBar(bossBar);
  }

  @Override
  public void executePlayEffect(@NotNull UUID player, @NotNull String entityEffect) {
  }

  @Override
  public void executeTeleport(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
  }

  @Override
  public void executeConnect(@NotNull UUID player, @NotNull String server) {
    ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
    ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
    if (proxiedPlayer != null && serverInfo != null) {
      proxiedPlayer.connect(serverInfo);
    }
  }
}
