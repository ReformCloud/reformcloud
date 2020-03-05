package systems.reformcloud.reformcloud2.executor.controller.network.packets.in;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.client.ClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.client.basic.DefaultClientRuntimeInformation;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.controller.process.ClientManager;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class ControllerPacketInClientAuthSuccess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return -45;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        if (packet.content().has("info")) {
            ClientRuntimeInformation clientRuntimeInformation = packet.content().get("info", new TypeToken<DefaultClientRuntimeInformation>() {});
            ClientManager.INSTANCE.connectClient(clientRuntimeInformation);
        }
    }
}
