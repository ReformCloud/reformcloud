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
package systems.reformcloud.reformcloud2.permissions.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.util.group.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;
import systems.reformcloud.reformcloud2.permissions.velocity.permission.DefaultPermissionProvider;

public class VelocityPermissionListener {

    @Subscribe
    public void handle(final LoginEvent event) {
        PermissionAPI.getInstance().getPermissionUtil().loadUser(
                event.getPlayer().getUniqueId(),
                event.getPlayer().getUsername()
        );
    }

    @Subscribe
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

    @Subscribe
    public void handle(final PermissionsSetupEvent event) {
        event.setProvider(DefaultPermissionProvider.INSTANCE);
    }

    @Subscribe
    public void handle(final DisconnectEvent event) {
        PermissionAPI.getInstance().getPermissionUtil().handleDisconnect(event.getPlayer().getUniqueId());
    }
}
