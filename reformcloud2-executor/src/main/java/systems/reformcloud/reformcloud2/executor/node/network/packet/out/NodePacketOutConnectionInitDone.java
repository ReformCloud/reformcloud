package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

public final class NodePacketOutConnectionInitDone extends JsonPacket {

    public NodePacketOutConnectionInitDone(@NotNull NodeInformation self) {
        super(NetworkUtil.NODE_TO_NODE_BUS + 18, new JsonConfiguration().add("info", self));
    }
}
