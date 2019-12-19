package systems.reformcloud.reformcloud2.executor.controller.packet.in;

import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.function.Consumer;

public final class ControllerPacketInUpdateProcessGroup implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 19;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        ProcessGroup processGroup = packet.content().get("group", ProcessGroup.TYPE);
        ExecutorAPI.getInstance().getSyncAPI().getGroupSyncAPI().updateProcessGroup(processGroup);
    }
}
