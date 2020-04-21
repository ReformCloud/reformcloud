package systems.reformcloud.reformcloud2.executor.controller.network.packets.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.ExecutorAPI;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.ExternalAPIImplementation;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;

import java.util.UUID;
import java.util.function.Consumer;

public class ControllerPacketInCopyProcess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return ExternalAPIImplementation.EXTERNAL_PACKET_ID + 38;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        String targetTemplate = packet.content().getOrDefault("targetTemplate", (String) null);
        String targetTemplateStorage = packet.content().getOrDefault("targetTemplateStorage", (String) null);
        String targetTemplateGroup = packet.content().getOrDefault("targetTemplateGroup", (String) null);

        String name = packet.content().getOrDefault("name", (String) null);
        if (name != null) {
            if (targetTemplate != null && targetTemplateStorage != null && targetTemplateGroup != null) {
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(
                        name, targetTemplate, targetTemplateStorage, targetTemplateGroup
                );
            } else if (targetTemplate != null && targetTemplateStorage != null) {
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(
                        name, targetTemplate, targetTemplateStorage
                );
            } else if (targetTemplate != null) {
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(
                        name, targetTemplate
                );
            } else {
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(name);
            }

            return;
        }

        UUID uniqueID = packet.content().get("uuid", UUID.class);
        if (uniqueID != null) {
            if (targetTemplate != null && targetTemplateStorage != null && targetTemplateGroup != null) {
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(
                        uniqueID, targetTemplate, targetTemplateStorage, targetTemplateGroup
                );
            } else if (targetTemplate != null && targetTemplateStorage != null) {
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(
                        uniqueID, targetTemplate, targetTemplateStorage
                );
            } else if (targetTemplate != null) {
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(
                        uniqueID, targetTemplate
                );
            } else {
                ExecutorAPI.getInstance().getSyncAPI().getProcessSyncAPI().copyProcess(uniqueID);
            }
        }
    }
}
