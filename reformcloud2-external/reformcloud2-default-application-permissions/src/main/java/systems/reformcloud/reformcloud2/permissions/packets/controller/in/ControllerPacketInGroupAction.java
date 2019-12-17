package systems.reformcloud.reformcloud2.permissions.packets.controller.in;

import java.util.function.Consumer;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;
import systems.reformcloud.reformcloud2.permissions.packets.util.PermissionAction;
import systems.reformcloud.reformcloud2.permissions.util.group.PermissionGroup;

public class ControllerPacketInGroupAction extends DefaultNetworkHandler {

  public ControllerPacketInGroupAction() {
    super(PacketHelper.PERMISSION_BUS + 1);
  }

  @Override
  public void handlePacket(PacketSender packetSender, Packet packet,
                           Consumer<Packet> responses) {
    final PermissionGroup permissionGroup =
        packet.content().get("group", PermissionGroup.TYPE);
    final PermissionAction action =
        packet.content().get("action", PermissionAction.class);

    if (permissionGroup == null) {
      return;
    }

    switch (action) {
    case CREATE: {
      PermissionAPI.getInstance().getPermissionUtil().createGroup(
          permissionGroup.getName());
      PermissionAPI.getInstance()
          .getPermissionUtil()
          .handleInternalPermissionGroupCreate(permissionGroup);
      break;
    }

    case UPDATE: {
      PermissionAPI.getInstance().getPermissionUtil().updateGroup(
          permissionGroup);
      PermissionAPI.getInstance()
          .getPermissionUtil()
          .handleInternalPermissionGroupUpdate(permissionGroup);
      break;
    }

    case DELETE: {
      PermissionAPI.getInstance().getPermissionUtil().deleteGroup(
          permissionGroup.getName());
      PermissionAPI.getInstance()
          .getPermissionUtil()
          .handleInternalPermissionGroupDelete(permissionGroup);
      break;
    }

    case DEFAULT_GROUPS_CHANGED: {
      PermissionAPI.getInstance().getPermissionUtil().removeDefaultGroup(
          permissionGroup.getName());
      PermissionAPI.getInstance()
          .getPermissionUtil()
          .handleInternalDefaultGroupsUpdate();
      break;
    }
    }
  }
}
