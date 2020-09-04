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
package systems.refomcloud.reformcloud2.embedded.plugin.gomint.executor;

import io.gomint.GoMint;
import io.gomint.entity.EntityPlayer;
import io.gomint.math.Location;
import io.gomint.world.Sound;
import io.gomint.world.World;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GoMintPlayerAPIExecutor extends PlayerAPIExecutor {

    @Override
    public void executeSendMessage(UUID player, String message) {
        EntityPlayer entityPlayer = GoMint.instance().findPlayerByUUID(player);
        if (entityPlayer != null) {
            entityPlayer.sendMessage(message);
        }
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        EntityPlayer entityPlayer = GoMint.instance().findPlayerByUUID(player);
        if (entityPlayer != null) {
            entityPlayer.disconnect(message);
        }
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
        EntityPlayer entityPlayer = GoMint.instance().findPlayerByUUID(player);
        Sound mintySound = CommonHelper.findEnumField(Sound.class, sound.toUpperCase()).orElse(null);
        if (entityPlayer != null && mintySound != null) {
            entityPlayer.playSound(entityPlayer.getLocation(), mintySound, (byte) f1);
        }
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        EntityPlayer entityPlayer = GoMint.instance().findPlayerByUUID(player);
        if (entityPlayer != null) {
            entityPlayer.sendTitle(title, subTitle, fadeIn * 20, stay * 20, fadeOut * 20, TimeUnit.SECONDS);
        }
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        EntityPlayer entityPlayer = GoMint.instance().findPlayerByUUID(player);
        World mintyWorld = GoMint.instance().getWorld(world);
        if (entityPlayer != null && mintyWorld != null) {
            entityPlayer.teleport(new Location(mintyWorld, (float) x, (float) y, (float) z, yaw, pitch));
        }
    }

    @Override
    public void executeConnect(UUID player, String server) {
    }
}
