package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public final class NodePacketOutExecuteCommand extends JsonPacket {

    public NodePacketOutExecuteCommand(String name, String command) {
        super(NetworkUtil.NODE_TO_NODE_BUS + 14, new JsonConfiguration().add("name", name).add("command", command));
    }
}
