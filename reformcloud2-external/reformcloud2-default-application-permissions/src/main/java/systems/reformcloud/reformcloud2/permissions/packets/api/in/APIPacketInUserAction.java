package systems.reformcloud.reformcloud2.permissions.packets.api.in;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;
import systems.reformcloud.reformcloud2.permissions.util.user.PermissionUser;

public class APIPacketInUserAction extends DefaultNetworkHandler {

  public APIPacketInUserAction() { super(PacketHelper.PERMISSION_BUS + 4); }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    final PermissionUser permissionUser =
        packet.content().get("user", PermissionUser.TYPE);
    final PermissionAction permissionAction =
        packet.content().get("action", PermissionAction.class);

    switch (permissionAction) {
    case CREATE: {
      PermissionAPI.getInstance().getPermissionUtil().handleInternalUserCreate(
          permissionUser);
      break;
    }

    case DELETE: {
      PermissionAPI.getInstance().getPermissionUtil().handleInternalUserDelete(
          permissionUser);
      break;
    }

    case UPDATE: {
      PermissionAPI.getInstance().getPermissionUtil().handleInternalUserUpdate(
          permissionUser);
      break;
    }
    }
  }
}
