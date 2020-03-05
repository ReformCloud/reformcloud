package systems.reformcloud.reformcloud2.executor.controller.network.packets.in.query;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.application.basic.DefaultLoadedApplication;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class ControllerQueryInGetApplications extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 4;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        responses.accept(new JsonPacket(-1, convert()));
    }

    private static JsonConfiguration convert() {
        return new JsonConfiguration().add("result", Streams.apply(ExecutorAPI.getInstance().getSyncAPI().getApplicationSyncAPI().getApplications(), application -> new DefaultLoadedApplication(application.loader(), application.applicationConfig(), application.mainClass())));
    }
}
