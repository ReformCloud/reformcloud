package systems.reformcloud.reformcloud2.permissions.packets.controller.in;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;
import systems.reformcloud.reformcloud2.permissions.util.basic.DefaultPermissionUtil;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

import java.util.function.Consumer;

public class ControllerPacketInUserAction extends DefaultNetworkHandler {

    public ControllerPacketInUserAction() {
        super(PacketHelper.PERMISSION_BUS + 3);
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        final PermissionUser permissionUser = packet.content().get("user", PermissionUser.TYPE);
        final PermissionAction action = packet.content().get("action", PermissionAction.class);

        switch (action) {
            case DELETE: {
                PermissionAPI.INSTANCE.getPermissionUtil().deleteUser(permissionUser.getUuid());
                break;
            }

            case UPDATE: {
                PermissionAPI.INSTANCE.getPermissionUtil().updateUser(permissionUser);
                break;
            }

            case CREATE: {
                ExecutorAPI.getInstance().insert(DefaultPermissionUtil.PERMISSION_PLAYER_TABLE, permissionUser.getUuid().toString(), null, new JsonConfiguration()
                        .add("user", permissionUser)
                );
                break;
            }

            default: {
                break;
            }
        }
    }
}
