package systems.reformcloud.reformcloud2.executor.node.network.packet.in;

import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessState;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.process.manager.LocalProcessManager;
import systems.reformcloud.reformcloud2.executor.node.process.startup.LocalProcessQueue;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class NodePacketInStartPreparedProcess extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 19;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        ProcessInformation processInformation = packet.content().get("info", ProcessInformation.TYPE);
        if (processInformation != null
                && processInformation.getNodeUniqueID() != null
                && processInformation.getNodeUniqueID().equals(NodeExecutor.getInstance().getNodeNetworkManager().getCluster().getSelfNode().getNodeUniqueID())
                && processInformation.getProcessState().equals(ProcessState.PREPARED)) {
            LocalProcessManager.getNodeProcesses()
                    .stream()
                    .filter(p -> p.getProcessInformation().getProcessUniqueID().equals(processInformation.getProcessUniqueID()))
                    .findFirst()
                    .ifPresent(LocalProcessQueue::queue);
        }
    }
}
