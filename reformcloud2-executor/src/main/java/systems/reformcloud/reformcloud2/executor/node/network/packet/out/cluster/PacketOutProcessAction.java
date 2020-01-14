package systems.reformcloud.reformcloud2.executor.node.network.packet.out.cluster;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;
import systems.reformcloud.reformcloud2.executor.node.process.util.ProcessAction;

public class PacketOutProcessAction extends JsonPacket {

    public PacketOutProcessAction(ProcessAction processAction, ProcessInformation processInformation) {
        super(NetworkUtil.NODE_TO_NODE_BUS + 4, new JsonConfiguration().add("action", processAction).add("info", processInformation));
    }
}
