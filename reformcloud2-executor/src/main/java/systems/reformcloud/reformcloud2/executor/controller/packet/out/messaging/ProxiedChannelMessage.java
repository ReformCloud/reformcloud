package systems.reformcloud.reformcloud2.executor.controller.packet.out.messaging;

import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import javax.annotation.Nonnull;

public class ProxiedChannelMessage extends JsonPacket {

    public ProxiedChannelMessage(@Nonnull JsonConfiguration content, @Nonnull String baseChannel, @Nonnull String subChannel) {
        super(NetworkUtil.MESSAGING_BUS + 2, new JsonConfiguration()
                .add("message", content)
                .add("base", baseChannel)
                .add("sub", subChannel)
        );
    }
}
