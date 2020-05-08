package systems.reformcloud.reformcloud2.permissions.packets;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;

public final class PacketHelper {

    private PacketHelper() {
        throw new UnsupportedOperationException();
    }

    public static final int PERMISSION_BUS = 8000;

    public static void addPacketHandler() {
        ExecutorAPI.getInstance().getPacketHandler().registerHandler(PacketGroupAction.class);
        ExecutorAPI.getInstance().getPacketHandler().registerHandler(PacketUserAction.class);
    }
}
