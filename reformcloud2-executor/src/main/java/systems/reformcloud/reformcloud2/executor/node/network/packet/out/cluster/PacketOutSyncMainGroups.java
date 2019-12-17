package systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster;

import java.util.Collection;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.MainGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.SyncAction;

public class PacketOutSyncMainGroups extends DefaultPacket {
  public PacketOutSyncMainGroups(Collection<MainGroup> mainGroups,
                                 SyncAction action) {
    super(NetworkUtil.NODE_TO_NODE_BUS + 8, new JsonConfiguration()
                                                .add("groups", mainGroups)
                                                .add("action", action));
  }
}
