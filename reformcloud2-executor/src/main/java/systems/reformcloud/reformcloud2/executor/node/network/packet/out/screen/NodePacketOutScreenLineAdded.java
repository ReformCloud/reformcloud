package systems.reformcloud.reformcloud2.executor.node.network.packet.out.screen;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public class NodePacketOutScreenLineAdded extends JsonPacket {

    public NodePacketOutScreenLineAdded(UUID uniqueID, String content) {
        super(NetworkUtil.NODE_TO_NODE_BUS + 15, new JsonConfiguration().add("uniqueID", uniqueID).add("line", content));
    }
}
