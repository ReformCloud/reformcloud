package de.klaro.reformcloud2.executor.controller.packet.in;

import de.klaro.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.network.NetworkUtil;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.controller.process.ClientManager;

import java.util.function.Consumer;

public final class ControllerPacketInNotifyRuntimeUpdate implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 4;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        DefaultClientRuntimeInformation clientRuntimeInformation = packet.content().get("info", ClientRuntimeInformation.TYPE);
        ClientManager.INSTANCE.updateClient(clientRuntimeInformation);
    }
}
