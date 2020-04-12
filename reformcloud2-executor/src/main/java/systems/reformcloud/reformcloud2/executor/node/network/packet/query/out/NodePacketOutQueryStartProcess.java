package systems.reformcloud.reformcloud2.executor.node.network.packet.query.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.process.api.ProcessConfiguration;

public class NodePacketOutQueryStartProcess extends JsonPacket {

    public NodePacketOutQueryStartProcess(ProcessConfiguration configuration, boolean start) {
        super(NetworkUtil.NODE_TO_NODE_QUERY_BUS + 1, new JsonConfiguration()
                .add("config", configuration)
                .add("start", start)
        );
    }
}
