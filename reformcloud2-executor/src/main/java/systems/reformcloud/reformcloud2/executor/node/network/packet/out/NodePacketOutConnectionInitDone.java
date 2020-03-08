package systems.reformcloud.reformcloud2.executor.node.network.packet.out;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;
import systems.reformcloud.reformcloud2.executor.api.common.node.NodeInformation;

import javax.annotation.Nonnull;

public final class NodePacketOutConnectionInitDone extends JsonPacket {

    public NodePacketOutConnectionInitDone(@Nonnull NodeInformation self) {
        super(NetworkUtil.NODE_TO_NODE_BUS + 18, new JsonConfiguration().add("info", self));
    }
}
