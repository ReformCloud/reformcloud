package de.klaro.reformcloud2.executor.controller.packet.in.query;

import de.klaro.reformcloud2.executor.api.common.ExecutorAPI;
import de.klaro.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import de.klaro.reformcloud2.executor.api.common.application.basic.DefaultLoadedApplication;
import de.klaro.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import de.klaro.reformcloud2.executor.api.common.network.channel.PacketSender;
import de.klaro.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import de.klaro.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import de.klaro.reformcloud2.executor.api.common.network.packet.Packet;
import de.klaro.reformcloud2.executor.api.common.utility.list.Links;

import java.util.function.Consumer;

public final class ControllerQueryInGetApplications implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 4;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        responses.accept(new DefaultPacket(-1, convert()));
    }

    private static JsonConfiguration convert() {
        return new JsonConfiguration().add("result", Links.apply(ExecutorAPI.getInstance().getApplications(), application -> new DefaultLoadedApplication(application.loader(), application.applicationConfig(), application.mainClass())));
    }
}
