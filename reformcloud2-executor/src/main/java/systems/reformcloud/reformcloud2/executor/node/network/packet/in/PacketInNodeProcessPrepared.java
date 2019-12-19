package systems.reformcloud.reformcloud2.executor.node.network.packet.in;

import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;
import java.util.function.Consumer;

public final class PacketInNodeProcessPrepared implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 10;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        UUID uuid = packet.content().get("uuid", UUID.class);
        String name = packet.content().getString("name");
        String template = packet.content().getString("template");

        System.out.println(LanguageManager.get(
                "process-prepared",
                name,
                uuid,
                template,
                packetSender.getName()
        ));
    }
}
