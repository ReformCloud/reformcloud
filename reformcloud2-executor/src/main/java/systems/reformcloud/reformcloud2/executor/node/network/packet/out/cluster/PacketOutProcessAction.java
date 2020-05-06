package systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStartedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessStoppedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.events.ProcessUpdatedEvent;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.EventPacketProcessClosed;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.EventPacketProcessStarted;
import systems.reformcloud.reformcloud2.executor.api.common.api.basic.packets.shared.EventPacketProcessUpdated;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.challenge.ChallengeAuthHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.NetworkChannelReader;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.data.ProtocolBuffer;
import systems.reformcloud.reformcloud2.executor.api.common.network.handler.ChannelReaderHelper;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;
import systems.reformcloud.reformcloud2.executor.node.cluster.sync.DefaultClusterSyncManager;
import systems.reformcloud.reformcloud2.executor.node.process.util.ProcessAction;

public class PacketOutProcessAction extends Packet {

    public PacketOutProcessAction() {
    }

    public PacketOutProcessAction(ProcessAction processAction, ProcessInformation processInformation) {
        this.processAction = processAction;
        this.processInformation = processInformation;
    }

    private ProcessAction processAction;

    private ProcessInformation processInformation;

    @Override
    public int getId() {
        return NetworkUtil.NODE_TO_NODE_BUS + 4;
    }

    @Override
    public void handlePacketReceive(@NotNull NetworkChannelReader reader, @NotNull ChallengeAuthHandler authHandler, @NotNull ChannelReaderHelper parent, @Nullable PacketSender sender, @NotNull ChannelHandlerContext channel) {
        switch (this.processAction) {
            case START: {
                NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessStart(
                        this.processInformation
                );

                NodeExecutor.getInstance().getEventManager().callEvent(new ProcessStartedEvent(this.processInformation));
                DefaultClusterSyncManager.sendToAllExcludedNodes(new EventPacketProcessStarted(this.processInformation));
                break;
            }

            case UPDATE: {
                NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessUpdate(
                        this.processInformation
                );

                NodeExecutor.getInstance().getEventManager().callEvent(new ProcessUpdatedEvent(this.processInformation));
                DefaultClusterSyncManager.sendToAllExcludedNodes(new EventPacketProcessUpdated(this.processInformation));
                break;
            }

            case STOP: {
                NodeExecutor.getInstance().getNodeNetworkManager().getNodeProcessHelper().handleProcessStop(
                        this.processInformation
                );

                NodeExecutor.getInstance().getEventManager().callEvent(new ProcessStoppedEvent(this.processInformation));
                DefaultClusterSyncManager.sendToAllExcludedNodes(new EventPacketProcessClosed(this.processInformation));
                break;
            }
        }
    }

    @Override
    public void write(@NotNull ProtocolBuffer buffer) {
        buffer.writeVarInt(this.processAction.ordinal());
        buffer.writeObject(this.processInformation);
    }

    @Override
    public void read(@NotNull ProtocolBuffer buffer) {
        this.processAction = ProcessAction.values()[buffer.readVarInt()];
        this.processInformation = buffer.readObject(ProcessInformation.class);
    }
}
