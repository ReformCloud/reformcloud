package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

public class NodePacketOutNodeInformationUpdate extends JsonPacket {

    public NodePacketOutNodeInformationUpdate(NodeInformation nodeInformation) {
        super(NetworkUtil.NODE_TO_NODE_BUS + 9, new JsonConfiguration().add("info", nodeInformation));
    }
}
