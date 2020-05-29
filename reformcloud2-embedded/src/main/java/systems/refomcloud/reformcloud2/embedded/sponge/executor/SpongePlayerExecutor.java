/*
 * MIT License
 *
 * Copyright (c) ReformCloud-Team
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
package systems.refomcloud.reformcloud2.embedded.sponge.executor;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;

import java.lang.reflect.Field;
import java.util.UUID;

public class SpongePlayerExecutor extends PlayerAPIExecutor {

    @Override
    public void executeSendMessage(UUID player, String message) {
        Sponge.getServer().getPlayer(player).ifPresent(val -> val.sendMessage(Text.of(message)));
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        Sponge.getServer().getPlayer(player).ifPresent(val -> val.kick(Text.of(message)));
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
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
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        Sponge.getServer().getPlayer(player).ifPresent(val -> {
            Title spongeTitle = Title
                    .builder()
                    .title(Text.of(title))
                    .subtitle(Text.of(subTitle))
                    .fadeIn(fadeIn)
                    .stay(stay)
                    .fadeOut(fadeOut)
                    .build();
            val.sendTitle(spongeTitle);
        });
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
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
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        Sponge.getServer().getPlayer(player).ifPresent(val -> Sponge.getServer().getWorld(world).ifPresent(w -> val.setLocation(new Location<>(w, x, y, z))));
    }

    @Override
    public void executeConnect(UUID player, String server) {
    }
}
