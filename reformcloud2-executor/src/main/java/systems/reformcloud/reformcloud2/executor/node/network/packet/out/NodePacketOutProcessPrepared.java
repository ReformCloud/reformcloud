package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.DefaultPacket;

import java.util.UUID;

public class NodePacketOutProcessPrepared extends DefaultPacket {

    public NodePacketOutProcessPrepared(String name, UUID uuid, String template) {
        super(NetworkUtil.CONTROLLER_INFORMATION_BUS + 6, new JsonConfiguration()
                .add("name", name)
                .add("uuid", uuid)
                .add("template", template)
        );
    }
}
