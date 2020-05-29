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
package systems.refomcloud.reformcloud2.embedded.spigot.executor;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import systems.refomcloud.reformcloud2.embedded.executor.PlayerAPIExecutor;
import systems.reformcloud.reformcloud2.executor.api.CommonHelper;

import java.util.UUID;

public class SpigotPlayerAPIExecutor extends PlayerAPIExecutor {

    @Override
    public void executeSendMessage(UUID player, String message) {
        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage(message);
        }
    }

    @Override
    public void executeKickPlayer(UUID player, String message) {
        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null) {
            bukkitPlayer.kickPlayer(message);
        }
    }

    @Override
    public void executePlaySound(UUID player, String sound, float f1, float f2) {
        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null) {
            bukkitPlayer.playSound(bukkitPlayer.getLocation(), sound, f1, f2);
        }
    }

    @Override
    public void executeSendTitle(UUID player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null) {
            bukkitPlayer.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
        }
    }

    @Override
    public void executePlayEffect(UUID player, String entityEffect) {
        Player bukkitPlayer = Bukkit.getPlayer(player);
        EntityEffect effect = CommonHelper.findEnumField(EntityEffect.class, entityEffect).orNothing();
        if (bukkitPlayer != null && effect != null) {
            bukkitPlayer.playEffect(effect);
        }
    }

    @Override
    public void executeTeleport(UUID player, String world, double x, double y, double z, float yaw, float pitch) {
        Player bukkitPlayer = Bukkit.getPlayer(player);
        World bukkitWorld = Bukkit.getWorld(world);
        if (bukkitPlayer != null && bukkitWorld != null) {
            bukkitPlayer.teleport(new Location(bukkitWorld, x, y, z, yaw, pitch));
        }
    }

    @Override
    public void executeConnect(UUID player, String server) {
    }
}
