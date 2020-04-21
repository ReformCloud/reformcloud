package systems.reformcloud.reformcloud2.executor.node.network.packet.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

import java.util.UUID;
import java.util.function.Consumer;

public class NodePacketInCopyProcess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.CONTROLLER_INFORMATION_BUS + 8;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        UUID uuid = packet.content().get("uuid", UUID.class);
        String targetTemplate = packet.content().getOrDefault("targetTemplate", (String) null);
        String targetTemplateStorage = packet.content().getOrDefault("targetTemplateStorage", (String) null);
        String targetTemplateGroup = packet.content().getOrDefault("targetTemplateGroup", (String) null);

        if (targetTemplate == null || targetTemplateStorage == null || targetTemplateGroup == null || uuid == null) {
            return;
        }

        Streams.filterToReference(
                LocalProcessManager.getNodeProcesses(),
                e -> e.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(uuid)
        ).ifPresent(e -> e.copy(targetTemplate, targetTemplateStorage, targetTemplateGroup));
    }
}
