package systems.reformcloud.reformcloud2.permissions.packets.api.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

import java.util.function.Consumer;

public class APIPacketInUserAction extends DefaultJsonNetworkHandler {

    public APIPacketInUserAction() {
        super(PacketHelper.PERMISSION_BUS + 4);
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        final PermissionUser permissionUser = packet.content().get("user", PermissionUser.TYPE);
        final PermissionAction permissionAction = packet.content().get("action", PermissionAction.class);

        switch (permissionAction) {
            case CREATE: {
                PermissionAPI.getInstance().getPermissionUtil().handleInternalUserCreate(permissionUser);
                break;
            }

            case DELETE: {
                PermissionAPI.getInstance().getPermissionUtil().handleInternalUserDelete(permissionUser);
                break;
            }

            case UPDATE: {
                PermissionAPI.getInstance().getPermissionUtil().handleInternalUserUpdate(permissionUser);
                break;
            }
        }
    }
}
