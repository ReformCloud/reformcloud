package de.klaro.reformcloud2.executor.controller.packet.in;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.controller.process.ClientManager;

import java.util.function.Consumer;

public final class ControllerPacketInClientAuthSuccess implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return -45;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        if (packet.content().has("info")) {
            ClientRuntimeInformation clientRuntimeInformation = packet.content().get("info", new TypeToken<DefaultClientRuntimeInformation>() {});
            ClientManager.INSTANCE.connectClient(clientRuntimeInformation);
        }
    }
}
