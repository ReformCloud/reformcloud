package de.klaro.reformcloud2.executor.controller.packet.in.query;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.application.InstallableApplication;
import de.klaro.reformcloud2.executor.api.common.application.basic.DefaultInstallableApplication;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.Consumer;

public final class ControllerQueryInLoadApplication implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 1;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        DefaultInstallableApplication application = packet.content().get("app", InstallableApplication.TYPE);
        responses.accept(new DefaultPacket(-1, new JsonConfiguration().add("installed", ExecutorAPI.getInstance().loadApplication(application))));
    }
}
