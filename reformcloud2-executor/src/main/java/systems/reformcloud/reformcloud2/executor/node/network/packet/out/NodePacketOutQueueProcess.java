package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.groups.ProcessGroup;
import systems.reformcloud.reformcloud2.executor.api.common.groups.template.Template;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.UUID;

public class NodePacketOutQueueProcess extends JsonPacket {

    public NodePacketOutQueueProcess(ProcessGroup processGroup, Template template, JsonConfiguration data, UUID uuid) {
        super(NetworkUtil.NODE_TO_NODE_BUS + 11, new JsonConfiguration()
                .add("group", processGroup)
                .add("template", template)
                .add("data", data)
                .add("uuid", uuid)
        );
    }
}
