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
package systems.reformcloud.reformcloud2.executor.api.spigot.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import systems.reformcloud.reformcloud2.executor.api.api.API;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.groups.utils.PlayerAccessConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.api.spigot.SpigotExecutor;

public final class PlayerListenerHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void handle(final PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        final PlayerAccessConfiguration configuration = current.getProcessGroup().getPlayerAccessConfiguration();

        if (configuration.isUseCloudPlayerLimit()
                && configuration.getMaxPlayers() < current.getProcessPlayerManager().getOnlineCount() + 1
                && !player.hasPermission(configuration.getFullJoinPermission())) {
            event.setKickMessage(format(
                    SpigotExecutor.getInstance().getMessages().getProcessFullMessage()
            ));
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            return;
        }

        if (configuration.isJoinOnlyPerPermission() && !player.hasPermission(configuration.getJoinPermission())) {
            event.setKickMessage(format(
                    SpigotExecutor.getInstance().getMessages().getProcessEnterPermissionNotSet()
            ));
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            return;
        }

        if (configuration.isMaintenance() && !player.hasPermission(configuration.getMaintenanceJoinPermission())) {
            event.setKickMessage(format(
                    SpigotExecutor.getInstance().getMessages().getProcessInMaintenanceMessage()
            ));
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            return;
        }

        if (current.getProcessDetail().getProcessState().equals(ProcessState.FULL) && !player.hasPermission(configuration.getFullJoinPermission())) {
            event.setKickMessage(format(
                    SpigotExecutor.getInstance().getMessages().getProcessFullMessage()
            ));
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            return;
        }

        if (current.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(player.getUniqueId())) {
            event.setKickMessage(format(
                    SpigotExecutor.getInstance().getMessages().getAlreadyConnectedMessage()
            ));
            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
            return;
        }

        if (Bukkit.getOnlinePlayers().size() >= current.getProcessDetail().getMaxPlayers()
                && !current.getProcessDetail().getProcessState().equals(ProcessState.FULL)
                && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
            current.getProcessDetail().setProcessState(ProcessState.FULL);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(final PlayerJoinEvent event) {
        final ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        current.getProcessPlayerManager().onLogin(event.getPlayer().getUniqueId(), event.getPlayer().getName());
        current.updateRuntimeInformation();
        SpigotExecutor.getInstance().setThisProcessInformation(current);
        ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(final PlayerQuitEvent event) {
        ProcessInformation current = API.getInstance().getCurrentProcessInformation();
        if (!current.getProcessPlayerManager().isPlayerOnlineOnCurrentProcess(event.getPlayer().getUniqueId())) {
            return;
        }

        Bukkit.getScheduler().runTask(SpigotExecutor.getInstance().getPlugin(), () -> {
            if (Bukkit.getOnlinePlayers().size() < current.getProcessDetail().getMaxPlayers()
                    && !current.getProcessDetail().getProcessState().equals(ProcessState.READY)
                    && !current.getProcessDetail().getProcessState().equals(ProcessState.INVISIBLE)) {
                current.getProcessDetail().setProcessState(ProcessState.READY);
            }

            current.updateRuntimeInformation();
            current.getProcessPlayerManager().onLogout(event.getPlayer().getUniqueId());
            SpigotExecutor.getInstance().setThisProcessInformation(current);
            ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().update(current);
        });
    }

    private String format(String msg) {
        return SpigotExecutor.getInstance().getMessages().format(msg);
    }
}
