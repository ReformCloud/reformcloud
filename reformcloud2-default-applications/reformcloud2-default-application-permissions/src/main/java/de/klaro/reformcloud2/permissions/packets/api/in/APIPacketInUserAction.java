package de.klaro.reformcloud2.permissions.packets.api.in;

import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.DefaultNetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.permissions.PermissionAPI;
import de.klaro.reformcloud2.permissions.packets.PacketHelper;
import de.klaro.reformcloud2.permissions.packets.util.PermissionAction;
import de.klaro.reformcloud2.permissions.util.user.PermissionUser;

import java.util.function.Consumer;

public class APIPacketInUserAction extends DefaultNetworkHandler {

    public APIPacketInUserAction() {
        super(PacketHelper.PERMISSION_BUS + 4);
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        final PermissionUser permissionUser = packet.content().get("user", PermissionUser.TYPE);
        final PermissionAction permissionAction = packet.content().get("action", PermissionAction.class);

        switch (permissionAction) {
            case CREATE: {
                PermissionAPI.INSTANCE.getPermissionUtil().handleInternalUserCreate(permissionUser);
                break;
            }

            case DELETE: {
                PermissionAPI.INSTANCE.getPermissionUtil().handleInternalUserDelete(permissionUser);
                break;
            }

            case UPDATE: {
                PermissionAPI.INSTANCE.getPermissionUtil().handleInternalUserUpdate(permissionUser);
                break;
            }
        }
    }
}
