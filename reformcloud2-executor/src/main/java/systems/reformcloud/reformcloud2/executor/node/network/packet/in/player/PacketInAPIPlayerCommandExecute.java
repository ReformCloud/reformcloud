package systems.reformcloud.reformcloud2.executor.node.network.packet.in.player;

import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;
import java.util.function.Consumer;

public final class PacketInAPIPlayerCommandExecute implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.PLAYER_INFORMATION_BUS + 5;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        String name = packet.content().getString("name");
        String command = packet.content().getString("command");
        UUID uuid = packet.content().get("uuid", UUID.class);

        System.out.println(LanguageManager.get(
                "player-executed-command",
                name,
                uuid,
                command,
                packetSender.getName()
        ));
    }
}
