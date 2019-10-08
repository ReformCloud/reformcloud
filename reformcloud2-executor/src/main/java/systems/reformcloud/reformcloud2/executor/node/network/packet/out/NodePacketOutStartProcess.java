package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

public class NodePacketOutStartProcess extends DefaultPacket {
    public NodePacketOutStartProcess(int id, JsonConfiguration content) {
        super(id, content);
    }
}
