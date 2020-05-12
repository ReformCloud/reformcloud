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
package systems.reformcloud.reformcloud2.tab;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;
import systems.reformcloud.reformcloud2.tab.listener.BukkitTabListeners;
import systems.reformcloud.reformcloud2.tab.listener.CloudTabListeners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ReformCloudTabPlugin extends JavaPlugin {

    private static Method setColor;

    static {
        try {
            setColor = Team.class.getDeclaredMethod("setColor", ChatColor.class);
            setColor.setAccessible(true);
        } catch (final NoSuchMethodException ignored) {
        }
    }

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("ReformCloud2BukkitPermissions") == null) {
            return;
        }

        Bukkit.getPluginManager().registerEvents(new BukkitTabListeners(), this);
        ExecutorAPI.getInstance().getEventManager().registerListener(new CloudTabListeners(this));
    }

    public static void pullPlayerNameTags(@NotNull Player player) {
        Conditions.isTrue(Bukkit.isPrimaryThread(), "Can only update the tab teams from the main thread");
        if (Bukkit.getScoreboardManager() == null) {
            return;
        }

        if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        PermissionGroup playerPermissionGroup = getOrDefault(player.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }

            if (playerPermissionGroup != null) {
                registerPlayerTeam(player, onlinePlayer, playerPermissionGroup);
            }

            PermissionGroup highestOnlinePlayerGroup = getOrDefault(onlinePlayer.getUniqueId());
            if (highestOnlinePlayerGroup != null) {
                registerPlayerTeam(onlinePlayer, player, highestOnlinePlayerGroup);
            }
        });
    }

    @Nullable
    private static PermissionGroup getOrDefault(@NotNull UUID uniqueID) {
        return PermissionAPI.getInstance()
                .getPermissionUtil()
                .loadUser(uniqueID)
                .getHighestPermissionGroup()
                .orElse(Streams.filter(PermissionAPI.getInstance().getPermissionUtil().getDefaultGroups(), e -> true));
    }

    private static void registerPlayerTeam(@NotNull Player player, @NotNull Player other, @NotNull PermissionGroup permissionGroup) {
        String teamName = permissionGroup.getPriority() + permissionGroup.getName();
        if (setColor != null && teamName.length() > 64) {
            teamName = teamName.substring(0, 64);
        } else if (setColor == null && teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
        }

        Team team = other.getScoreboard().getTeam(teamName);
        if (team == null) {
            team = other.getScoreboard().registerNewTeam(teamName);
        }

        if (setColor != null) {
            try {
                String color = permissionGroup.getColour().orElse(null);
                if (color != null && !color.isEmpty()) {
                    ChatColor chatColor = ChatColor.getByChar(color.replace("&", "").replace("§", ""));
                    if (chatColor != null) {
                        setColor.invoke(team, chatColor);
                    }
                } else if (permissionGroup.getPrefix().isPresent()) {
                    color = ChatColor.getLastColors(permissionGroup.getPrefix().get());
                    if (!color.isEmpty()) {
                        ChatColor chatColor = ChatColor.getByChar(color.replace("&", "").replace("§", ""));
                        if (chatColor != null) {
                            setColor.invoke(team, chatColor);
                        }
                    }
                }
            } catch (final IllegalAccessException | InvocationTargetException exception) {
                exception.printStackTrace();
            }
        }

        team.setPrefix(ChatColor.translateAlternateColorCodes('&', permissionGroup.getPrefix().orElse("")));
        team.setSuffix(ChatColor.translateAlternateColorCodes('&', permissionGroup.getSuffix().orElse("")));
        team.addEntry(player.getName());
        player.setDisplayName(ChatColor.translateAlternateColorCodes('&', permissionGroup.getDisplay().orElse("") + player.getName()));
    }
}
