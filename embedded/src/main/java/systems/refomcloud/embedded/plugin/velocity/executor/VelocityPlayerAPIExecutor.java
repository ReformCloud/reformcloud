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
package systems.refomcloud.embedded.plugin.velocity.executor;

import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.executor.PlayerAPIExecutor;

import java.util.UUID;

public class VelocityPlayerAPIExecutor extends PlayerAPIExecutor {

  private final ProxyServer proxyServer;

  public VelocityPlayerAPIExecutor(@NotNull ProxyServer proxyServer) {
    this.proxyServer = proxyServer;
  }

  @Override
  public void executeSendMessage(@NotNull UUID player, @NotNull Component message) {
    this.proxyServer.getPlayer(player).ifPresent(val -> val.sendMessage(message));
  }

  @Override
  public void executeKickPlayer(@NotNull UUID player, @NotNull Component message) {
    this.proxyServer.getPlayer(player).ifPresent(val -> val.disconnect(message));
  }

  @Override
  public void executePlaySound(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
  }

  @Override
  public void executeSendTitle(@NotNull UUID player, @NotNull Title title) {
    this.proxyServer.getPlayer(player).ifPresent(val -> val.showTitle(title));
  }

  @Override
  public void executeSendActionBar(@NotNull UUID player, @NotNull Component actionBar) {
    this.proxyServer.getPlayer(player).ifPresent(val -> val.sendActionBar(actionBar));
  }

  @Override
  public void executeSendBossBar(@NotNull UUID player, @NotNull BossBar bossBar) {
    this.proxyServer.getPlayer(player).ifPresent(val -> val.showBossBar(bossBar));
  }

  @Override
  public void executeHideBossBar(@NotNull UUID player, @NotNull BossBar bossBar) {
    this.proxyServer.getPlayer(player).ifPresent(val -> val.hideBossBar(bossBar));
  }

  @Override
  public void executePlayEffect(@NotNull UUID player, @NotNull String entityEffect) {
  }

  @Override
  public void executeTeleport(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
  }

  @Override
  public void executeConnect(@NotNull UUID player, @NotNull String server) {
    this.proxyServer.getPlayer(player).ifPresent(val -> this.proxyServer.getServer(server).ifPresent(s -> val.createConnectionRequest(s).fireAndForget()));
  }
}
