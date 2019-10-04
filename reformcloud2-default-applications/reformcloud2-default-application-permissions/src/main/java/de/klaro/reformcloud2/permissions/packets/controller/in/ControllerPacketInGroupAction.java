package de.klaro.reformcloud2.permissions.packets.controller.in;

import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.DefaultNetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.permissions.PermissionAPI;
import de.klaro.reformcloud2.permissions.packets.PacketHelper;
import de.klaro.reformcloud2.permissions.packets.controller.out.ControllerPacketOutGroupAction;
import de.klaro.reformcloud2.permissions.packets.util.PermissionAction;
import de.klaro.reformcloud2.permissions.util.group.PermissionGroup;

import java.util.function.Consumer;

public class ControllerPacketInGroupAction extends DefaultNetworkHandler {

    public ControllerPacketInGroupAction() {
        super(PacketHelper.PERMISSION_BUS + 1);
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        final PermissionGroup permissionGroup = packet.content().get("group", PermissionGroup.TYPE);
        final PermissionAction action = packet.content().get("action", PermissionAction.class);
        DefaultChannelManager.INSTANCE.getAllSender().forEach(sender -> sender.sendPacket(new ControllerPacketOutGroupAction(permissionGroup, action)));

        switch (action) {
            case CREATE: {
                PermissionAPI.INSTANCE.getPermissionUtil().createGroup(permissionGroup.getName());
                PermissionAPI.INSTANCE.getPermissionUtil().handleInternalPermissionGroupCreate(permissionGroup);
                break;
            }

            case UPDATE: {
                PermissionAPI.INSTANCE.getPermissionUtil().updateGroup(permissionGroup);
                PermissionAPI.INSTANCE.getPermissionUtil().handleInternalPermissionGroupUpdate(permissionGroup);
                break;
            }

            case DELETE: {
                PermissionAPI.INSTANCE.getPermissionUtil().deleteGroup(permissionGroup.getName());
                PermissionAPI.INSTANCE.getPermissionUtil().handleInternalPermissionGroupDelete(permissionGroup);
                break;
            }
        }
    }
}
