package systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.SyncAction;

import java.util.Collection;

public class PacketOutSyncMainGroups extends JsonPacket {
    public PacketOutSyncMainGroups(Collection<MainGroup> mainGroups, SyncAction action) {
        super(NetworkUtil.NODE_TO_NODE_BUS + 8, new JsonConfiguration().add("groups", mainGroups).add("action", action));
    }
}
