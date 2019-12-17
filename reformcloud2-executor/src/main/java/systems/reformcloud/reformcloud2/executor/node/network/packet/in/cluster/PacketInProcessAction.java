package systems.reformcloud.reformcloud2.executor.node.network.packet.in.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.event.ControllerEventProcessClosed;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.event.ControllerEventProcessStarted;
import systems.reformcloud.reformcloud2.executor.controller.packet.out.event.ControllerEventProcessUpdated;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.cluster.sync.DefaultClusterSyncManager;
import systems.reformcloud.reformcloud2.executor.node.process.util.ProcessAction;

import java.util.function.Consumer;

public class PacketInProcessAction implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 4;
    }

    @Override
    public void handlePacket(PacketSender packetSender, Packet packet, Consumer<Packet> responses) {
        ProcessAction action = packet.content().get("action", ProcessAction.class);
        ProcessInformation information = packet.content().get("info", ProcessInformation.TYPE);

        switch (action) {
            case START: {
                NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessStart(
                        information
                );
                NodeExecutor.getInstance().getNodeNetworkManager().getQueuedProcesses().remove(information.getProcessUniqueID());
                DefaultClusterSyncManager.sendToAllExcludedNodes(new ControllerEventProcessStarted(information));
                break;
            }

            case UPDATE: {
                NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessUpdate(
                        information
                );
                DefaultClusterSyncManager.sendToAllExcludedNodes(new ControllerEventProcessUpdated(information));
                break;
            }

            case STOP: {
                NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessStop(
                        information
                );
                DefaultClusterSyncManager.sendToAllExcludedNodes(new ControllerEventProcessClosed(information));
                break;
            }
        }
    }
}
