package systems.reformcloud.reformcloud2.executor.node.network.packet.in.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventProcessClosed;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventProcessStarted;
import systems.reformcloud.reformcloud2.executor.controller.network.packets.out.event.ControllerEventProcessUpdated;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.cluster.sync.DefaultClusterSyncManager;
import systems.reformcloud.reformcloud2.executor.node.process.util.ProcessAction;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class PacketInProcessAction extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 4;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        ProcessAction action = packet.content().get("action", ProcessAction.class);
        ProcessInformation information = packet.content().get("info", ProcessInformation.TYPE);

        if (action == null || information == null) {
            System.err.println(LanguageManager.get("network-received-invalid-packet", getClass().getSimpleName(), packetSender.getName()));
            return;
        }

        switch (action) {
            case START: {
                NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessStart(
                        information
                );
                NodeExecutor.getInstance().getNodeNetworkManager().getQueuedProcesses().remove(information.getProcessUniqueID());

                NodeExecutor.getInstance().getEventManager().callEvent(new ProcessStartedEvent(information));
                DefaultClusterSyncManager.sendToAllExcludedNodes(new ControllerEventProcessStarted(information));
                break;
            }

            case UPDATE: {
                NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessUpdate(
                        information
                );

                NodeExecutor.getInstance().getEventManager().callEvent(new ProcessUpdatedEvent(information));
                DefaultClusterSyncManager.sendToAllExcludedNodes(new ControllerEventProcessUpdated(information));
                break;
            }

            case STOP: {
                NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessStop(
                        information
                );

                NodeExecutor.getInstance().getEventManager().callEvent(new ProcessStoppedEvent(information));
                DefaultClusterSyncManager.sendToAllExcludedNodes(new ControllerEventProcessClosed(information));
                break;
            }
        }
    }
}
