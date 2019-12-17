package systems.reformcloud.reformcloud2.permissions.packets.api.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;

public class APIPacketOutGroupAction extends DefaultPacket {

  public APIPacketOutGroupAction(PermissionGroup group,
                                 PermissionAction action) {
    super(PacketHelper.PERMISSION_BUS + 1,
          new JsonConfiguration().add("group", group).add("action", action));
  }
}
