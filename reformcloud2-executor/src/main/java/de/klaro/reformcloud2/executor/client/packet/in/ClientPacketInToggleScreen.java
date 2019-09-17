package de.klaro.reformcloud2.executor.client.packet.in;

import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;
import de.klaro.reformcloud2.executor.client.ClientExecutor;
import de.klaro.reformcloud2.executor.client.screen.ProcessScreen;

import java.util.UUID;
import java.util.function.Consumer;

public final class ClientPacketInToggleScreen implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 10;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        UUID uuid = packet.content().get("uuid", UUID.class);

        Links.filterToOptional(
                ClientExecutor.getInstance().getScreenManager().getPerProcessScreenLines(),
                uuid::equals
        ).ifPresent(ProcessScreen::toggleScreen);
    }
}
