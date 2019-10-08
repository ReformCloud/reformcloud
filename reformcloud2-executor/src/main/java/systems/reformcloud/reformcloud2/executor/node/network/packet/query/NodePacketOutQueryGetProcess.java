package systems.reformcloud.reformcloud2.executor.node.network.packet.query;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public class NodePacketOutQueryGetProcess extends DefaultPacket {

    public NodePacketOutQueryGetProcess(String name) {
        super(NetworkUtil.NODE_TO_NODE_QUERY_BUS + 1, new JsonConfiguration().add("name", name));
    }

    public NodePacketOutQueryGetProcess(UUID uniqueID) {
        super(NetworkUtil.NODE_TO_NODE_QUERY_BUS + 1, new JsonConfiguration().add("uniqueID", uniqueID));
    }
}
