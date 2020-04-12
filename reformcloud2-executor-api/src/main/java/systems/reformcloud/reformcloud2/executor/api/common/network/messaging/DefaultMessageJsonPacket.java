package systems.reformcloud.reformcloud2.executor.api.common.network.messaging;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ReceiverType;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import java.util.Collection;

public class DefaultMessageJsonPacket extends JsonPacket {

    public DefaultMessageJsonPacket(@NotNull JsonConfiguration content, @NotNull Collection<String> receivers,
                                    @NotNull ErrorReportHandling handling, @NotNull String baseChannel, @NotNull String subChannel) {
        super(NetworkUtil.MESSAGING_BUS + 1, new JsonConfiguration()
                .add("receivers", receivers)
                .add("handling", handling)
                .add("message", content)
                .add("base", baseChannel)
                .add("sub", subChannel)
        );
    }

    public DefaultMessageJsonPacket(@NotNull Collection<ReceiverType> receivers, @NotNull JsonConfiguration content,
                                    @NotNull String baseChannel, @NotNull String subChannel) {
        super(NetworkUtil.MESSAGING_BUS + 1, new JsonConfiguration()
                .add("receivers", receivers)
                .add("message", content)
                .add("base", baseChannel)
                .add("sub", subChannel)
        );
    }
}
