package systems.reformcloud.reformcloud2.executor.controller.packet.out.messaging;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import javax.annotation.Nonnull;

public class ProxiedChannelMessage extends JsonPacket {

    public ProxiedChannelMessage(@Nonnull JsonConfiguration content) {
        super(NetworkUtil.MESSAGING_BUS + 2, content);
    }
}
