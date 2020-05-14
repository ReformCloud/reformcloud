package systems.reformcloud.reformcloud2.executor.node.network.packet.in.cluster;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.DefaultJsonNetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.SyncAction;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Consumer;

public class PacketInSyncProcessGroups extends DefaultJsonNetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 3;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        Collection<ProcessGroup> groups = packet.content().get("info", new TypeToken<Collection<ProcessGroup>>() {
        });
        SyncAction action = packet.content().get("action", SyncAction.class);

        NodeExecutor.getInstance().getClusterSyncManager().handleProcessGroupSync(groups, action);
    }
}
