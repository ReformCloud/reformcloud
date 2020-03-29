package systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.serialisation.PacketReader;

import java.util.function.Consumer;

public interface NetworkHandler extends PacketReader {

    /**
     * @return The packet id which this handler is going to handle
     */
    int getHandlingPacketID();

    /**
     * Gets called when a packet with the given id is received
     *
     * @param packetSender The packet sender who sent the packet
     * @param packet       The packet itself which got sent
     * @param responses    The response which should be sent to the sender
     */
    void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses);
}
