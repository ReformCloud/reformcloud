package systems.reformcloud.reformcloud2.permissions.packets.api.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

public class APIPacketOutUserAction extends DefaultPacket {

  public APIPacketOutUserAction(PermissionUser user, PermissionAction action) {
    super(PacketHelper.PERMISSION_BUS + 3,
          new JsonConfiguration().add("user", user).add("action", action));
  }
}
