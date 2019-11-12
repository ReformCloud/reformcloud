package systems.reformcloud.reformcloud2.permissions.packets.controller.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

public class ControllerPacketOutUserAction extends DefaultPacket {

    public ControllerPacketOutUserAction(PermissionUser user, PermissionAction action) {
        super(PacketHelper.PERMISSION_BUS + 4, new JsonConfiguration().add("user", user).add("action", action));
    }
}
