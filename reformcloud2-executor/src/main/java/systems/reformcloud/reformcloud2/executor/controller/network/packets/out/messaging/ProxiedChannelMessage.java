package systems.reformcloud.reformcloud2.executor.controller.network.packets.out.messaging;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

public class ProxiedChannelMessage extends JsonPacket {

    public ProxiedChannelMessage(@NotNull JsonConfiguration content, @NotNull String baseChannel, @NotNull String subChannel) {
        super(NetworkUtil.MESSAGING_BUS + 2, new JsonConfiguration()
                .add("message", content)
                .add("base", baseChannel)
                .add("sub", subChannel)
        );
    }
}
