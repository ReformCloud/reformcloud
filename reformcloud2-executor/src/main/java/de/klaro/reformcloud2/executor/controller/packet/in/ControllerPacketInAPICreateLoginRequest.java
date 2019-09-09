package de.klaro.reformcloud2.executor.controller.packet.in;

import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.controller.join.OnlyProxyJoinHelper;

import java.util.UUID;
import java.util.function.Consumer;

public final class ControllerPacketInAPICreateLoginRequest implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.PLAYER_INFORMATION_BUS + 1;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        UUID uuid = packet.content().get("uniqueID", UUID.class);
        String name = packet.content().getString("name");

        OnlyProxyJoinHelper.createRequest(uuid, name);
    }
}
