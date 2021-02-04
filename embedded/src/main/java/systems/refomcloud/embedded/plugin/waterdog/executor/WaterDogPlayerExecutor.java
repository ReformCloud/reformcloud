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
package systems.refomcloud.embedded.plugin.waterdog.executor;

import dev.waterdog.ProxyServer;
import dev.waterdog.network.ServerInfo;
import dev.waterdog.player.ProxiedPlayer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.executor.PlayerAPIExecutor;

import java.util.UUID;

public class WaterDogPlayerExecutor extends PlayerAPIExecutor {

  @Override
  public void executeSendMessage(@NotNull UUID player, @NotNull Component message) {
    final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
    if (proxiedPlayer != null && message instanceof TextComponent) {
      proxiedPlayer.sendMessage(((TextComponent) message).content());
    }
  }

  @Override
  public void executeKickPlayer(@NotNull UUID player, @NotNull Component message) {
    final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
    if (proxiedPlayer != null && message instanceof TextComponent) {
      proxiedPlayer.disconnect(((TextComponent) message).content());
    }
  }

  @Override
  public void executePlaySound(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
  }

  @Override
  public void executeSendTitle(@NotNull UUID player, @NotNull Title title) {
    final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
    if (proxiedPlayer != null && title.title() instanceof TextComponent && title.subtitle() instanceof TextComponent) {
      final Title.Times times = title.times();
      proxiedPlayer.sendTitle(
        ((TextComponent) title.title()).content(),
        ((TextComponent) title.subtitle()).content(),
        times == null ? 20 : (int) times.fadeIn().toMillis() / 50,
        times == null ? 20 : (int) times.stay().toMillis() / 50,
        times == null ? 5 : (int) times.fadeOut().toMillis() / 50
      );
    }
  }

  @Override
  public void executeSendActionBar(@NotNull UUID player, @NotNull Component actionBar) {
  }

  @Override
  public void executeSendBossBar(@NotNull UUID player, @NotNull BossBar bossBar) {
  }

  @Override
  public void executeHideBossBar(@NotNull UUID player, @NotNull BossBar bossBar) {
  }

  @Override
  public void executePlayEffect(@NotNull UUID player, @NotNull String entityEffect) {
  }

  @Override
  public void executeTeleport(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
  }

  @Override
  public void executeConnect(@NotNull UUID player, @NotNull String server) {
    final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
    final ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
    if (proxiedPlayer != null && serverInfo != null) {
      proxiedPlayer.connect(serverInfo);
    }
  }
}
