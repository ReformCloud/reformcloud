package systems.reformcloud.reformcloud2.executor.node.network.packet.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.running.RunningProcess;
import systems.reformcloud.reformcloud2.executor.api.common.utility.list.Streams;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;

import java.util.UUID;
import java.util.function.Consumer;

public class PacketInNodeStopProcess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 1;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        UUID uniqueID = packet.content().get("uniqueID", UUID.class);
        RunningProcess information = Streams.filterToReference(LocalProcessManager.getNodeProcesses(),
                e -> e.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(uniqueID)
        ).orNothing();

        if (information == null) {
            return;
        }

        information.shutdown();
    }
}
