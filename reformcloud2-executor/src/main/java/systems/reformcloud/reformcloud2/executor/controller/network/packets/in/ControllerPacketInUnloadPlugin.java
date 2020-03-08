package systems.reformcloud.reformcloud2.executor.controller.network.packets.in;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.Plugin;
import systems.reformcloud.reformcloud2.executor.api.common.plugins.basic.DefaultPlugin;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class ControllerPacketInUnloadPlugin extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 28;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        String process = packet.content().getString("process");
        DefaultPlugin defaultPlugin = packet.content().get("plugin", Plugin.TYPE);
        ExecutorAPI.getInstance().getSyncAPI().getPluginSyncAPI().unloadPlugin(process, defaultPlugin);
    }
}
