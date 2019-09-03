package de.klaro.reformcloud2.executor.controller.packet.in.query;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.application.LoadedApplication;
import de.klaro.reformcloud2.executor.api.common.application.basic.DefaultLoadedApplication;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.Consumer;

public final class ControllerQueryInGetLoadedApplication implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 3;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        String name = packet.content().getString("name");
        responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("result", convert(ExecutorAPI.getInstance().getApplication(name)))));
    }

    private static DefaultLoadedApplication convert(LoadedApplication application) {
        return new DefaultLoadedApplication(application.loader(), application.applicationConfig(), application.mainClass());
    }
}
