package systems.reformcloud.reformcloud2.executor.node.network.packet.in.api;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.Consumer;

public final class PacketInSendRawLine implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 7;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        ExecutorAPI.getInstance().getSyncAPI().getConsoleSyncAPI().sendRawLine(packet.content().getString("line"));
    }
}
