package systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class PacketOutReloadCluster extends DefaultPacket {

    public PacketOutReloadCluster() {
        super(NetworkUtil.NODE_TO_NODE_BUS + 2, new JsonConfiguration());
    }
}
