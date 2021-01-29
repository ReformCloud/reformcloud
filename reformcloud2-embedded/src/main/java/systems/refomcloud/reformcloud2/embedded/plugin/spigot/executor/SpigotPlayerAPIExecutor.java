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
package systems.refomcloud.reformcloud2.embedded.plugin.spigot.executor;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.craftbukkit.BukkitComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.enums.EnumUtil;

import java.util.UUID;

public class SpigotPlayerAPIExecutor extends PlayerAPIExecutor {

  private final BukkitAudiences bukkitAudiences;

  public SpigotPlayerAPIExecutor(@NotNull Plugin plugin) {
    this.bukkitAudiences = BukkitAudiences.create(plugin);
  }

  @Override
  public void executeSendMessage(@NotNull UUID player, @NotNull Component message) {
    this.bukkitAudiences.player(player).sendMessage(message);
  }

  @Override
  public void executeKickPlayer(@NotNull UUID player, @NotNull Component message) {
    Player bukkitPlayer = Bukkit.getPlayer(player);
    if (bukkitPlayer != null) {
      bukkitPlayer.kickPlayer(BukkitComponentSerializer.legacy().serialize(message));
    }
  }

  @Override
  public void executePlaySound(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
    Player bukkitPlayer = Bukkit.getPlayer(player);
    if (bukkitPlayer != null) {
      bukkitPlayer.playSound(bukkitPlayer.getLocation(), sound, f1, f2);
    }
  }

  @Override
  public void executeSendTitle(@NotNull UUID player, @NotNull Title title) {
    this.bukkitAudiences.player(player).showTitle(title);
  }

  @Override
  public void executeSendActionBar(@NotNull UUID player, @NotNull Component actionBar) {
    this.bukkitAudiences.player(player).sendActionBar(actionBar);
  }

  @Override
  public void executeSendBossBar(@NotNull UUID player, @NotNull BossBar bossBar) {
    this.bukkitAudiences.player(player).showBossBar(bossBar);
  }

  @Override
  public void executeHideBossBar(@NotNull UUID player, @NotNull BossBar bossBar) {
    this.bukkitAudiences.player(player).hideBossBar(bossBar);
  }

  @Override
  public void executePlayEffect(@NotNull UUID player, @NotNull String entityEffect) {
    Player bukkitPlayer = Bukkit.getPlayer(player);
    EntityEffect effect = EnumUtil.findEnumFieldByName(EntityEffect.class, entityEffect).orElse(null);
    if (bukkitPlayer != null && effect != null) {
      bukkitPlayer.playEffect(effect);
    }
  }

  @Override
  public void executeTeleport(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
    Player bukkitPlayer = Bukkit.getPlayer(player);
    World bukkitWorld = Bukkit.getWorld(world);
    if (bukkitPlayer != null && bukkitWorld != null) {
      bukkitPlayer.teleport(new Location(bukkitWorld, x, y, z, yaw, pitch));
    }
  }

  @Override
  public void executeConnect(@NotNull UUID player, @NotNull String server) {
  }
}
