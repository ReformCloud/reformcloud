package systems.reformcloud.reformcloud2.permissions.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.permission.PermissionsSetupEvent;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Links;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.util.group.NodeGroup;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;
import systems.reformcloud.reformcloud2.permissions.velocity.permission.DefaultPermissionProvider;

public class VelocityPermissionListener {

    @Subscribe
    public void handle(final PostLoginEvent event) {
        final PermissionUser permissionUser = PermissionAPI.INSTANCE.getPermissionUtil().loadUser(event.getPlayer().getUniqueId());
        Task.EXECUTOR.execute(() -> {
            PermissionAPI.INSTANCE.getPermissionUtil().getDefaultGroups().forEach(e -> {
                if (Links.filterToReference(permissionUser.getGroups(), g -> g.getGroupName().equals(e.getName())).isPresent()) {
                    return;
                }

                permissionUser.getGroups().add(new NodeGroup(
                        System.currentTimeMillis(),
                        -1,
                        e.getName()
                ));
            });

            PermissionAPI.INSTANCE.getPermissionUtil().updateUser(permissionUser);
        });
    }

    @Subscribe
    public void handle(final PermissionsSetupEvent event) {
        event.setProvider(DefaultPermissionProvider.INSTANCE);
    }

    @Subscribe
    public void handle(final DisconnectEvent event) {
        PermissionAPI.INSTANCE.getPermissionUtil().handleDisconnect(event.getPlayer().getUniqueId());
    }
}
