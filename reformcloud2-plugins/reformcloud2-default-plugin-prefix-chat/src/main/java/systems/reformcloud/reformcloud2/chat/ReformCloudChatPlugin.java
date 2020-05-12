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
package systems.reformcloud.reformcloud2.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;

public class ReformCloudChatPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("ReformCloud2BukkitPermissions") == null) {
            return;
        }

        super.getConfig().options().copyDefaults(true);
        super.saveConfig();
        this.chatFormat = super.getConfig().getString("format", "%display%%name% &7➤ &f%message%");

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private String chatFormat;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(final @NotNull AsyncPlayerChatEvent event) {
        PermissionAPI.getInstance().getPermissionUtil().loadUser(event.getPlayer().getUniqueId()).getHighestPermissionGroup().ifPresent(permissionGroup -> {
            String message = event.getMessage().replace("%", "%%");
            if (ChatColor.stripColor(message).trim().isEmpty()) {
                event.setCancelled(true);
                return;
            }

            if (event.getPlayer().hasPermission("reformcloud.chat.coloured")) {
                message = ChatColor.translateAlternateColorCodes('&', message);
            }

            String finalFormat = this.chatFormat
                    .replace("%name%", event.getPlayer().getName())
                    .replace("%player_display%", event.getPlayer().getDisplayName())
                    .replace("%group%", permissionGroup.getName())
                    .replace("%priority%", Integer.toString(permissionGroup.getPriority()))
                    .replace("%prefix%", permissionGroup.getPrefix().orElse(""))
                    .replace("%suffix%", permissionGroup.getSuffix().orElse(""))
                    .replace("%display%", permissionGroup.getDisplay().orElse(""))
                    .replace("%colour%", permissionGroup.getColour().orElse(""));
            finalFormat = ChatColor.translateAlternateColorCodes('&', finalFormat);
            event.setFormat(finalFormat.replace("%message%", message));
        });
    }
}
