package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.process.ProcessInformation;

public class PacketOutHeadNodeStartProcess extends JsonPacket {

    public PacketOutHeadNodeStartProcess(ProcessInformation processInformation) {
        super(NetworkUtil.NODE_TO_NODE_BUS + 11, new JsonConfiguration().add("info", processInformation));
    }
}
