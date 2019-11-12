package systems.reformcloud.reformcloud2.permissions.packets.api.in;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;

import java.util.function.Consumer;

public class APIPacketInGroupAction extends DefaultNetworkHandler {

    public APIPacketInGroupAction() {
        super(PacketHelper.PERMISSION_BUS + 2);
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        final PermissionGroup permissionGroup = packet.content().get("group", PermissionGroup.TYPE);
        final PermissionAction permissionAction = packet.content().get("action", PermissionAction.class);

        switch (permissionAction) {
            case UPDATE: {
                PermissionAPI.INSTANCE.getPermissionUtil().handleInternalPermissionGroupUpdate(permissionGroup);
                break;
            }

            case DELETE: {
                PermissionAPI.INSTANCE.getPermissionUtil().handleInternalPermissionGroupDelete(permissionGroup);
                break;
            }

            case CREATE: {
                PermissionAPI.INSTANCE.getPermissionUtil().handleInternalPermissionGroupCreate(permissionGroup);
                break;
            }

            case DEFAULT_GROUPS_CHANGED: {
                PermissionAPI.INSTANCE.getPermissionUtil().handleInternalDefaultGroupsUpdate();
                break;
            }
        }
    }
}
