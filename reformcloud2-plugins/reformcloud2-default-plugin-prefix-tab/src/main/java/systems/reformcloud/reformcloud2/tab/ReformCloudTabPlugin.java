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
import systems.reformcloud.reformcloud2.executor.api.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.base.Conditions;
import systems.reformcloud.reformcloud2.executor.api.event.EventManager;
import systems.reformcloud.reformcloud2.permissions.PermissionManagement;
import systems.reformcloud.reformcloud2.permissions.objects.group.PermissionGroup;
import systems.reformcloud.reformcloud2.permissions.objects.user.PermissionUser;
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
            System.err.println("[Tab] Unable to find permission plugin, do not load permission listeners");
            return;
        }

        Bukkit.getPluginManager().registerEvents(new BukkitTabListeners(), this);
        ExecutorAPI.getInstance().getServiceRegistry().getProviderUnchecked(EventManager.class).registerListener(new CloudTabListeners(this));
    }

    public static void pullPlayerNameTags(@NotNull Player player) {
        Conditions.isTrue(Bukkit.isPrimaryThread(), "Can only update the tab teams from the main thread");
        if (Bukkit.getScoreboardManager() == null) {
            return;
        }

        if (player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        PermissionUser permissionUser = getUser(player.getUniqueId());
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (onlinePlayer.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
                onlinePlayer.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }

            if (permissionUser != null) {
                registerPlayerTeam(player, onlinePlayer, permissionUser);
            }

            PermissionUser onlinePermissionUser = getUser(onlinePlayer.getUniqueId());
            if (onlinePermissionUser != null) {
                registerPlayerTeam(onlinePlayer, player, onlinePermissionUser);
            }
        });
    }

    @Nullable
    private static PermissionUser getUser(@NotNull UUID uniqueID) {
        return PermissionManagement.getInstance().getExistingUser(uniqueID).orElse(null);
    }

    private static void registerPlayerTeam(@NotNull Player player, @NotNull Player other, @NotNull PermissionUser permissionUser) {
        int priority = 0;
        String teamName = player.getName();

        PermissionGroup permissionGroup = permissionUser.getHighestPermissionGroup().orElse(null);
        if (permissionGroup != null) {
            priority = permissionGroup.getPriority();
        }

        teamName = priority + teamName;
        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
        }

        Team team = other.getScoreboard().getTeam(teamName);
        if (team == null) {
            team = other.getScoreboard().registerNewTeam(teamName);
        }

        if (setColor != null) {
            try {
                String color = permissionUser.getColour().orElse(null);
                if (color != null && !color.isEmpty()) {
                    ChatColor chatColor = ChatColor.getByChar(color.replace("&", "").replace("§", ""));
                    if (chatColor != null) {
                        setColor.invoke(team, chatColor);
                    }
                } else if (permissionUser.getPrefix().isPresent()) {
                    color = ChatColor.getLastColors(permissionUser.getPrefix().get());
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

        String prefix = permissionUser.getPrefix().orElse(null);
        if (prefix != null) {
            prefix = ChatColor.translateAlternateColorCodes('&', prefix);
            if (setColor != null && prefix.length() > 64) {
                prefix = prefix.substring(0, 64);
            } else if (setColor == null && prefix.length() > 16) {
                prefix = prefix.substring(0, 16);
            }

            team.setPrefix(prefix);
        }

        String suffix = permissionUser.getSuffix().orElse(null);
        if (suffix != null) {
            suffix = ChatColor.translateAlternateColorCodes('&', suffix);
            if (setColor != null && suffix.length() > 64) {
                suffix = suffix.substring(0, 64);
            } else if (setColor == null && suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }

            team.setSuffix(suffix);
        }

        team.addEntry(player.getName());
        player.setDisplayName(ChatColor.translateAlternateColorCodes('&', permissionUser.getPrefix().orElse("") + player.getName()));
    }
}