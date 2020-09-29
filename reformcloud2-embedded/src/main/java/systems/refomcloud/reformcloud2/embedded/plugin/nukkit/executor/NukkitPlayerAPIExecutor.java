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
package systems.refomcloud.reformcloud2.embedded.plugin.nukkit.executor;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.enums.EnumUtil;

import java.util.UUID;

public class NukkitPlayerAPIExecutor extends PlayerAPIExecutor {

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
        Sound nukkitSound = EnumUtil.findEnumFieldByName(Sound.class, sound).orElse(null);
        if (nukkitSound == null) {
            return;
        }

        Server.getInstance().getPlayer(player).ifPresent(val -> val.getLevel().addSound(val.getLocation().getPosition(), nukkitSound, f1, f2, val));
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        Server.getInstance().getPlayer(player).ifPresent(val -> val.sendTitle(title, subTitle, fadeIn, stay, fadeOut));
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
        Server.getInstance().getPlayer(player).ifPresent(val -> {
            Identifier effect = this.getEffectByName(entityEffect);
            if (effect == null) {
                return;
            }

            Level level = val.getLevel();
            level.addParticleEffect(val.getLocation().getPosition(), effect, -1L, level.getDimension(), val);
        });
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

    private @Nullable Identifier getEffectByName(@NotNull String name) {
        try {
            return (Identifier) Identifier.class.getField(name.toUpperCase()).get(null);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            return null;
        }
    }
}
