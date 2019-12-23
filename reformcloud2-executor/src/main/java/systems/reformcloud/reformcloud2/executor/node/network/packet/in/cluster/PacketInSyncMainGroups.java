package systems.reformcloud.reformcloud2.executor.node.network.packet.in.cluster;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.PacketSender;
import systems.reformcloud.reformcloud2.executor.api.common.network.channel.handler.NetworkHandler;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.Packet;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.SyncAction;
import systems.reformcloud.reformcloud2.executor.node.NodeExecutor;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Consumer;

public class PacketInSyncMainGroups implements NetworkHandler {

    @Override
    public int getHandlingPacketID() {
        return NetworkUtil.NODE_TO_NODE_BUS + 8;
    }

    @Override
    public void handlePacket(@Nonnull PacketSender packetSender, @Nonnull Packet packet, @Nonnull Consumer<Packet> responses) {
        Collection<MainGroup> groups = packet.content().get("groups", new TypeToken<Collection<MainGroup>>() {});
        SyncAction action = packet.content().get("action", SyncAction.class);

        NodeExecutor.getInstance().getClusterSyncManager().handleMainGroupSync(groups, action);
    }
}
