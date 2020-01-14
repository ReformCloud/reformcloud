package systems.reformcloud.reformcloud2.executor.node.network.packet.in.api;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class PacketInSendColouredLine extends DefaultJsonNetworkHandler {
    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 6;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        try {
            ExecutorAPI.getInstance().getSyncAPI().getConsoleSyncAPI().sendColouredLine(packet.content().getString("line"));
        } catch (final IllegalAccessException ignored) {
            //Ignore this because it is not caused by a direct controller mistake (but eg by developer not the owner)
        }
    }
}
