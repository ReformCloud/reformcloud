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
package systems.refomcloud.reformcloud2.embedded.plugin.sponge.executor;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.spongeapi.SpongeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.spongeapi.SpongeComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.world.Location;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;

import java.lang.reflect.Field;
import java.util.UUID;

public class SpongePlayerExecutor extends PlayerAPIExecutor {

  private final SpongeAudiences spongeAudiences;

  public SpongePlayerExecutor(SpongeAudiences spongeAudiences) {
    this.spongeAudiences = spongeAudiences;
  }

  @Override
  public void executeSendMessage(@NotNull UUID player, @NotNull Component message) {
    this.spongeAudiences.player(player).sendMessage(message);
  }

  @Override
  public void executeKickPlayer(@NotNull UUID player, @NotNull Component message) {
    Sponge.getServer().getPlayer(player).ifPresent(val -> val.kick(SpongeComponentSerializer.get().serialize(message)));
  }

  @Override
  public void executePlaySound(@NotNull UUID player, @NotNull String sound, float f1, float f2) {
    Sponge.getServer().getPlayer(player).ifPresent(val -> {
      try {
        Field field = SoundTypes.class.getDeclaredField(sound);
        field.setAccessible(true);

        val.getWorld().playSound((SoundType) field.get(null), val.getPosition(), f1, f2);
      } catch (final NoSuchFieldException exception) {
        System.err.println("Unable to play sound " + sound + " is does not exist?");
      } catch (final IllegalAccessException exception) {
        exception.printStackTrace();
      }
    });
  }

  @Override
  public void executeSendTitle(@NotNull UUID player, net.kyori.adventure.title.@NotNull Title title) {
    this.spongeAudiences.player(player).showTitle(title);
  }

  @Override
  public void executeSendActionBar(@NotNull UUID player, @NotNull Component actionBar) {
    this.spongeAudiences.player(player).sendActionBar(actionBar);
  }

  @Override
  public void executeSendBossBar(@NotNull UUID player, @NotNull BossBar bossBar) {
    this.spongeAudiences.player(player).showBossBar(bossBar);
  }

  @Override
  public void executeHideBossBar(@NotNull UUID player, @NotNull BossBar bossBar) {
    this.spongeAudiences.player(player).hideBossBar(bossBar);
  }

  @Override
  public void executePlayEffect(@NotNull UUID player, @NotNull String entityEffect) {
    Sponge.getServer().getPlayer(player).ifPresent(val -> {
      try {
        Field field = ParticleTypes.class.getDeclaredField(entityEffect);
        field.setAccessible(true);

        ParticleEffect effect = ParticleEffect.builder().type((ParticleType) field.get(null)).build();
        val.spawnParticles(effect, val.getPosition());
      } catch (final NoSuchFieldException exception) {
        System.err.println("Unable to play effect " + entityEffect + " is does not exist?");
      } catch (final IllegalAccessException exception) {
        exception.printStackTrace();
      }
    });
  }

  @Override
  public void executeTeleport(@NotNull UUID player, @NotNull String world, double x, double y, double z, float yaw, float pitch) {
    Sponge.getServer().getPlayer(player).ifPresent(val -> Sponge.getServer().getWorld(world).ifPresent(w -> val.setLocation(new Location<>(w, x, y, z))));
  }

  @Override
  public void executeConnect(@NotNull UUID player, @NotNull String server) {
  }
}
