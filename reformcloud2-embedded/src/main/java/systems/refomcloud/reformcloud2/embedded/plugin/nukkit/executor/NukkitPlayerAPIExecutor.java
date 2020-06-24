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
package systems.refomcloud.reformcloud2.embedded.plugin.nukkit.executor;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Sound;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;

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
        Server.getInstance().getPlayer(player).ifPresent(val -> {
            Sound nukkitSound = CommonHelper.findEnumField(Sound.class, sound).orNothing();
            if (nukkitSound == null) {
                return;
            }

            val.getLevel().addSound(val.getLocation(), nukkitSound, f1, f2, val);
        });
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        Server.getInstance().getPlayer(player).ifPresent(val -> val.sendTitle(title, subTitle, fadeIn, stay, fadeOut));
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
        Server.getInstance().getPlayer(player).ifPresent(val -> {
            ParticleEffect effect = CommonHelper.findEnumField(ParticleEffect.class, entityEffect).orNothing();
            if (effect == null) {
                return;
            }

            val.getLevel().addParticleEffect(val.getLocation(), effect);
        });
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        Server.getInstance().getPlayer(player).ifPresent(val -> {
            Level level = Server.getInstance().getLevelByName(world);
            if (level != null) {
                val.teleport(new Location(x, y, z, yaw, pitch, level));
            }
        });
    }

    @Override
    public void executeConnect(UUID player, String server) {
    }
}
