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
package systems.refomcloud.reformcloud2.embedded.plugin.cloudburst.executor;

import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.Location;
import org.cloudburstmc.server.level.ParticleEffectIds;
import org.cloudburstmc.server.level.Sound;
import org.cloudburstmc.server.utils.Identifier;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.enums.EnumUtil;

import java.lang.reflect.Field;
import java.util.UUID;

public class CloudBurstPlayerAPIExecutor extends PlayerAPIExecutor {

  @Override
  public void executeSendMessage(UUID player, String message) {
    Server.getInstance().getPlayer(player).ifPresent(val -> val.sendMessage(message));
  }

  @Override
  public void executeKickPlayer(UUID player, String message) {
    Server.getInstance().getPlayer(player).ifPresent(val -> val.kick(message));
  }

  @Override
  public void executePlaySound(UUID player, String sound, float f1, float f2) {
    Sound cloudSound = EnumUtil.findEnumFieldByName(Sound.class, sound).orElse(null);
    if (cloudSound == null) {
      return;
    }

    Server.getInstance().getPlayer(player).ifPresent(val -> val.getLevel().addSound(val.getPosition(), cloudSound, f1, f2, val));
  }

  @Override
  public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
    Server.getInstance().getPlayer(player).ifPresent(val -> val.sendTitle(title, subTitle, fadeIn, stay, fadeOut));
  }

  @Override
  public void executePlayEffect(UUID player, String entityEffect) {
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
  public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
    Server.getInstance().getPlayer(player).ifPresent(val -> {
      Level level = Server.getInstance().getLevelByName(world);
      if (level != null) {
        val.teleport(Location.from((float) x, (float) y, (float) z, yaw, pitch, level));
      }
    });
  }

  @Override
  public void executeConnect(UUID player, String server) {
  }
}
