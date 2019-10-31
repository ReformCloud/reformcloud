package systems.reformcloud.reformcloud2.executor.node.network.packet.out.screen;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public class NodePacketOutToggleScreen extends DefaultPacket {

    public NodePacketOutToggleScreen(UUID uniqueID) {
        super(NetworkUtil.NODE_TO_NODE_BUS + 17, new JsonConfiguration().add("uniqueID", uniqueID));
    }
}
