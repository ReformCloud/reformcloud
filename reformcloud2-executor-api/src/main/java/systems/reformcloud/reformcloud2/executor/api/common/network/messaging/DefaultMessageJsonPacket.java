package systems.reformcloud.reformcloud2.executor.api.common.network.messaging;

import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ReceiverType;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.network.NetworkUtil;
import systems.reformcloud.reformcloud2.executor.api.common.network.packet.JsonPacket;

import javax.annotation.Nonnull;
import java.util.Collection;

public class DefaultMessageJsonPacket extends JsonPacket {

    public DefaultMessageJsonPacket(@Nonnull JsonConfiguration content, @Nonnull Collection<String> receivers,
                                    @Nonnull ErrorReportHandling handling, @Nonnull String baseChannel, @Nonnull String subChannel) {
        super(NetworkUtil.MESSAGING_BUS + 1, new JsonConfiguration()
                .add("receivers", receivers)
                .add("handling", handling)
                .add("message", content)
                .add("base", baseChannel)
                .add("sub", subChannel)
        );
    }

    public DefaultMessageJsonPacket(@Nonnull Collection<ReceiverType> receivers, @Nonnull JsonConfiguration content,
                                    @Nonnull String baseChannel, @Nonnull String subChannel) {
        super(NetworkUtil.MESSAGING_BUS + 1, new JsonConfiguration()
                .add("receivers", receivers)
                .add("message", content)
                .add("base", baseChannel)
                .add("sub", subChannel)
        );
    }
}
