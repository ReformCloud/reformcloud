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
package systems.reformcloud.reformcloud2.permissions.bungeecord.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.util.group.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

public class BungeeCordPermissionListener implements Listener {

    @EventHandler
    public void handle(final LoginEvent event) {
        // Push user into name to unique ID DB
        PermissionAPI.getInstance().getPermissionUtil().loadUser(
                event.getConnection().getUniqueId(),
                event.getConnection().getName()
        );
    }

    @EventHandler
    public void handle(final PostLoginEvent event) {
        final PermissionUser permissionUser = PermissionAPI.getInstance().getPermissionUtil().loadUser(event.getPlayer().getUniqueId());
        Task.EXECUTOR.execute(() -> {
            PermissionAPI.getInstance().getPermissionUtil().getDefaultGroups().forEach(e -> {
                if (Streams.filterToReference(permissionUser.getGroups(), g -> g.getGroupName().equals(e.getName())).isPresent()) {
                    return;
                }

                permissionUser.getGroups().add(new NodeGroup(
                        System.currentTimeMillis(),
                        -1,
                        e.getName()
                ));
            });

            PermissionAPI.getInstance().getPermissionUtil().updateUser(permissionUser);
        });
    }

    @EventHandler
    public void handle(final PermissionCheckEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        final PermissionUser permissionUser = PermissionAPI.getInstance().getPermissionUtil().loadUser(player.getUniqueId());
        event.setHasPermission(permissionUser.hasPermission(event.getPermission()));
    }

    @EventHandler
    public void handle(final PlayerDisconnectEvent event) {
        PermissionAPI.getInstance().getPermissionUtil().handleDisconnect(event.getPlayer().getUniqueId());
    }
}
