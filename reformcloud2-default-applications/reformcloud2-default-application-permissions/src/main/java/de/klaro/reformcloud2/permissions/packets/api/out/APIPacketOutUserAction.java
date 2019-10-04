package de.klaro.reformcloud2.permissions.packets.api.out;

import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.permissions.packets.PacketHelper;
import de.klaro.reformcloud2.permissions.packets.util.PermissionAction;
import de.klaro.reformcloud2.permissions.util.user.PermissionUser;

public class APIPacketOutUserAction extends DefaultPacket {

    public APIPacketOutUserAction(PermissionUser user, PermissionAction action) {
        super(PacketHelper.PERMISSION_BUS + 3, new JsonConfiguration().add("user", user).add("action", action));
    }
}
