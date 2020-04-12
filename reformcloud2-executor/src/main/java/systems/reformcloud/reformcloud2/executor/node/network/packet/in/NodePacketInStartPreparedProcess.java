package systems.reformcloud.reformcloud2.executor.node.network.packet.in;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;
import systems.reformcloud.reformcloud2.executor.node.process.startup.LocalProcessQueue;

import java.util.function.Consumer;

public class NodePacketInStartPreparedProcess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 19;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        ProcessInformation processInformation = packet.content().get("info", ProcessInformation.TYPE);
        if (processInformation != null
                && processInformation.getProcessDetail().getParentUniqueID().equals(NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getNodeUniqueID())
                && processInformation.getProcessDetail().getProcessState().equals(ProcessState.PREPARED)) {
            LocalProcessManager.getNodeProcesses()
                    .stream()
                    .filter(p -> p.getProcessInformation().getProcessDetail().getProcessUniqueID().equals(processInformation.getProcessDetail().getProcessUniqueID()))
                    .findFirst()
                    .ifPresent(LocalProcessQueue::queue);
        }
    }
}
