package systems.reformcloud.reformcloud2.permissions.util;

import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.manager.DefaultChannelManager;
import systems.reformcloud.reformcloud2.executor.api.common.utility.optional.ReferencedOptional;
import systems.reformcloud.reformcloud2.executor.api.common.utility.thread.AbsoluteThread;
import systems.reformcloud.reformcloud2.permissions.PermissionAPI;
import systems.reformcloud.reformcloud2.permissions.packets.PacketHelper;

public final class PermissionPluginUtil {

    private PermissionPluginUtil() {
        throw new UnsupportedOperationException();
    }

    public static void awaitConnection() {
        NetworkUtil.EXECUTOR.execute(() -> {
            ReferencedOptional<PacketSender> optionalPacketSender = DefaultChannelManager.INSTANCE.get("Controller");
            while (!optionalPacketSender.isPresent()) {
                AbsoluteThread.sleep(5);
                optionalPacketSender = DefaultChannelManager.INSTANCE.get("Controller");
            }

            PermissionAPI.handshake();
            PacketHelper.addAPIPackets();
        });
    }

}
