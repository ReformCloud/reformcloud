package systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler;

import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.Consumer;

public interface NetworkHandler {

    int getHandlingPacketID();

    void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses);
}
