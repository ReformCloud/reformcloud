package systems.reformcloud.reformcloud2.permissions.packets;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.permissions.packets.api.in.APIPacketInGroupAction;
import systems.reformcloud.reformcloud2.permissions.packets.api.in.APIPacketInUserAction;
import systems.reformcloud.reformcloud2.permissions.packets.controller.in.ControllerPacketInGroupAction;
import systems.reformcloud.reformcloud2.permissions.packets.controller.in.ControllerPacketInUserAction;

public final class PacketHelper {

    public static final int PERMISSION_BUS = 8000;

    private PacketHelper() {
        throw new UnsupportedOperationException();
    }

    public static void addControllerPackets() {
        ExecutorAPI.getInstance().getPacketHandler().registerNetworkHandlers(
                new ControllerPacketInGroupAction(),
                new ControllerPacketInUserAction()
        );
    }

    public static void addAPIPackets() {
        ExecutorAPI.getInstance().getPacketHandler().registerNetworkHandlers(
                new APIPacketInGroupAction(),
                new APIPacketInUserAction()
        );
    }

    public static void unregisterControllerPackets() {
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(PacketHelper.PERMISSION_BUS + 2);
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(PacketHelper.PERMISSION_BUS + 4);
    }

    public static void unregisterAPIPackets() {
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(PacketHelper.PERMISSION_BUS + 1);
        ExecutorAPI.getInstance().getPacketHandler().unregisterNetworkHandlers(PacketHelper.PERMISSION_BUS + 3);
    }
}
