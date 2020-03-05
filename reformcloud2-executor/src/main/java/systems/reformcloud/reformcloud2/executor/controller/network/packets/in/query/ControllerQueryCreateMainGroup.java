package systems.reformcloud.reformcloud2.executor.controller.network.packets.in.query;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public final class ControllerQueryCreateMainGroup extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 16;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        MainGroup mainGroup = packet.content().get("group", MainGroup.TYPE);
        responses.accept(new JsonPacket(-1, new JsonConfiguration().add("result", ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().createMainGroup(
                mainGroup.getName(), mainGroup.getSubGroups()
        ))));
    }
}
