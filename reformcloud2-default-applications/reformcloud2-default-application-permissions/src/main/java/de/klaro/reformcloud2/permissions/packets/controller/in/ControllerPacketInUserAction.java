package de.klaro.reformcloud2.permissions.packets.controller.in;

import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.DefaultNetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.permissions.PermissionAPI;
import de.klaro.reformcloud2.permissions.packets.PacketHelper;
import de.klaro.reformcloud2.permissions.packets.controller.out.ControllerPacketOutUserAction;
import de.klaro.reformcloud2.permissions.packets.util.PermissionAction;
import de.klaro.reformcloud2.permissions.util.user.PermissionUser;

import java.util.function.Consumer;

public class ControllerPacketInUserAction extends DefaultNetworkHandler {

    public ControllerPacketInUserAction() {
        super(PacketHelper.PERMISSION_BUS + 3);
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        final PermissionUser permissionUser = packet.content().get("user", PermissionUser.TYPE);
        final PermissionAction action = packet.content().get("action", PermissionAction.class);
        DefaultChannelManager.INSTANCE.getAllSender().forEach(sender -> sender.sendPacket(new ControllerPacketOutUserAction(permissionUser, action)));

        switch (action) {
            case DELETE: {
                PermissionAPI.INSTANCE.getPermissionUtil().deleteUser(permissionUser.getUuid());
                break;
            }

            case UPDATE: {
                PermissionAPI.INSTANCE.getPermissionUtil().updateUser(permissionUser);
                break;
            }

            case CREATE:
            default: {
                break;
            }
        }
    }
}
