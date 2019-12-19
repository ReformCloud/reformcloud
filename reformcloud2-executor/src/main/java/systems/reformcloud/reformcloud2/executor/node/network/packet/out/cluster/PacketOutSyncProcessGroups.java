package systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster;

import java.util.Collection;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;
import systems.reformcloud.reformcloud2.executor.api.node.cluster.SyncAction;

public class PacketOutSyncProcessGroups extends DefaultPacket {

  public PacketOutSyncProcessGroups(Collection<ProcessGroup> groups,
                                    SyncAction action) {
    super(NetworkUtil.NODE_TO_NODE_BUS + 3,
          new JsonConfiguration().add("info", groups).add("action", action));
  }
}
