package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.Collection;

public class NodePacketOutSyncGroups extends DefaultPacket {

    public NodePacketOutSyncGroups(Collection<MainGroup> mainGroups, Collection<ProcessGroup> processGroups) {
        super(NetworkUtil.NODE_TO_NODE_BUS + 7, new JsonConfiguration().add("main", mainGroups).add("sub", processGroups));
    }
}
