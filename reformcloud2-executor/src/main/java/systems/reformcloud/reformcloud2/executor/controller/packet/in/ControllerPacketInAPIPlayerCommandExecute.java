package systems.reformcloud.reformcloud2.executor.controller.packet.in;

import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Consumer;

public final class ControllerPacketInAPIPlayerCommandExecute implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.PLAYER_INFORMATION_BUS + 5;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
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
