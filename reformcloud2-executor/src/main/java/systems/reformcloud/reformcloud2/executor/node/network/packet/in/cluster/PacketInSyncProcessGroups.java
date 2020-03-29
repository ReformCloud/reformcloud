package systems.reformcloud.reformcloud2.executor.node.network.packet.in.cluster;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.SyncAction;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import java.util.Collection;
import java.util.function.Consumer;

public class PacketInSyncProcessGroups extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 3;
    }

    @Override
    public void handlePacket(@NotNull PacketSender packetSender, @NotNull Packet packet, @NotNull Consumer<Packet> responses) {
        Collection<ProcessGroup> groups = packet.content().get("info", new TypeToken<Collection<ProcessGroup>>() {});
        SyncAction action = packet.content().get("action", SyncAction.class);

        NodeExecutor.getInstance().getClusterSyncManager().handleProcessGroupSync(groups, action);
    }
}
