package de.klaro.reformcloud2.permissions.packets.controller.out;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.permissions.packets.PacketHelper;
import de.klaro.reformcloud2.permissions.packets.util.PermissionAction;
import de.klaro.reformcloud2.permissions.util.group.PermissionGroup;

public class ControllerPacketOutGroupAction extends DefaultPacket {

    public ControllerPacketOutGroupAction(PermissionGroup group, PermissionAction action) {
        super(PacketHelper.PERMISSION_BUS + 2, new JsonConfiguration().add("group", group).add("action", action));
    }
}
