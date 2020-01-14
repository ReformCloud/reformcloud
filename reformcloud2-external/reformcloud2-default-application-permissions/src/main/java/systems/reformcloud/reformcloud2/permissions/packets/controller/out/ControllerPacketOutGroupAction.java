package systems.reformcloud.reformcloud2.permissions.packets.controller.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;

public class ControllerPacketOutGroupAction extends JsonPacket {

    public ControllerPacketOutGroupAction(PermissionGroup group, PermissionAction action) {
        super(PacketHelper.PERMISSION_BUS + 2, new JsonConfiguration().add("group", group).add("action", action));
    }
}
