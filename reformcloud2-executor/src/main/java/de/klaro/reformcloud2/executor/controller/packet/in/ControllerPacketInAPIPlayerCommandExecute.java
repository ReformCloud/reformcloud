package de.klaro.reformcloud2.executor.controller.packet.in;

import de.klaro.reformcloud2.executor.api.common.language.LanguageManager;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;
import java.util.function.Consumer;

public final class ControllerPacketInAPIPlayerCommandExecute implements NetworkHandler {

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
