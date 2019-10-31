package systems.reformcloud.reformcloud2.executor.node.network.packet.in.screen;

import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.node.process.log.NodeProcessScreenHandler;

import java.util.UUID;
import java.util.function.Consumer;

public class PacketInToggleScreen implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 17;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        UUID uniqueID = packet.content().get("uniqueID", UUID.class);
        NodeProcessScreenHandler.getScreen(uniqueID).ifPresent(e -> e.toggleFor(packetSender.getName()));
    }
}
