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
package systems.refomcloud.embedded.plugin.cloudburst.executor;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.Location;
import org.cloudburstmc.server.level.ParticleEffectIds;
import org.cloudburstmc.server.level.Sound;
import org.cloudburstmc.server.utils.DummyBossBar;
import org.cloudburstmc.server.utils.Identifier;
import org.jetbrains.annotations.NotNull;
import systems.refomcloud.embedded.executor.PlayerAPIExecutor;
import systems.reformcloud.enums.EnumUtil;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class CloudBurstPlayerAPIExecutor extends PlayerAPIExecutor {

  @Override
  public void executeSendMessage(@NotNull UUID player, @NotNull Component message) {
    if (message instanceof TextComponent) {
      Server.getInstance().getPlayer(player).ifPresent(val -> val.sendMessage(((TextComponent) message).content()));
    }
  }

  @Override
  public void executeKickPlayer(@NotNull UUID player, @NotNull Component message) {
    if (message instanceof TextComponent) {
      Server.getInstance().getPlayer(player).ifPresent(val -> val.kick(((TextComponent) message).content()));
    }
  }

  @Override
  public void executePlaySound(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
    Sound cloudSound = EnumUtil.findEnumFieldByName(Sound.class, sound).orElse(null);
    if (cloudSound == null) {
      return;
    }

    Server.getInstance().getPlayer(player).ifPresent(val -> val.getLevel().addSound(val.getPosition(), cloudSound, f1, f2, val));
  }

  @Override
  public void executeSendTitle(@NotNull UUID player, @NotNull Title title) {
    if (title.title() instanceof TextComponent && title.subtitle() instanceof TextComponent) {
      final Title.Times times = title.times();
      Server.getInstance().getPlayer(player).ifPresent(val -> val.sendTitle(
        ((TextComponent) title.title()).content(),
        ((TextComponent) title.subtitle()).content(),
        times == null ? 20 : (int) times.fadeIn().toMillis() / 50,
        times == null ? 50 : (int) times.stay().toMillis() / 50,
        times == null ? 20 : (int) times.fadeOut().toMillis() / 50
      ));
    }
  }

  @Override
  public void executeSendActionBar(@NotNull UUID player, @NotNull Component actionBar) {
    if (actionBar instanceof TextComponent) {
      Server.getInstance().getPlayer(player).ifPresent(val -> val.sendActionBar(((TextComponent) actionBar).content()));
    }
  }

  @Override
  public void executeSendBossBar(@NotNull UUID player, @NotNull BossBar bossBar) {
    if (bossBar instanceof TextComponent) {
      Server.getInstance().getPlayer(player).ifPresent(val -> val.createBossBar(new DummyBossBar.Builder(val).text(((TextComponent) bossBar).content()).build()));
    }
  }

  @Override
  public void executeHideBossBar(@NotNull UUID player, @NotNull BossBar bossBar) {
    if (bossBar instanceof TextComponent) {
      Server.getInstance().getPlayer(player).ifPresent(val -> {
        for (Map.Entry<Long, DummyBossBar> entry : val.getDummyBossBars().entrySet()) {
          if (entry.getValue().getText().equals(((TextComponent) bossBar).content())) {
            val.removeBossBar(entry.getKey());
            break;
          }
        }
      });
    }
  }

  @Override
  public void executePlayEffect(@NotNull UUID player, @NotNull String entityEffect) {
    try {
      Field field = ParticleEffectIds.class.getDeclaredField(entityEffect.toUpperCase());
      field.setAccessible(true);
      Identifier identifier = (Identifier) field.get(null);

      Server.getInstance().getPlayer(player).ifPresent(val -> {
        val.getLevel().addParticleEffect(
          val.getPosition(),
          identifier,
          val.getRuntimeId(),
          val.getLevel().getDimension(),
          val
        );
      });
    } catch (NoSuchFieldException | IllegalAccessException | ClassCastException ignored) {
    }
  }

  @Override
  public void executeTeleport(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
    Server.getInstance().getPlayer(player).ifPresent(val -> {
      Level level = Server.getInstance().getLevelByName(world);
      if (level != null) {
        val.teleport(Location.from((float) x, (float) y, (float) z, yaw, pitch, level));
      }
    });
  }

  @Override
  public void executeConnect(@NotNull UUID player, @NotNull String server) {
  }
}
