package systems.reformcloud.reformcloud2.executor.api.common.api.messaging;

import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import javax.annotation.Nonnull;

public interface MessageSyncAPI {

    default void sendChannelMessageSync(@Nonnull String receiver, @Nonnull JsonConfiguration jsonConfiguration) {
        sendChannelMessageSync(jsonConfiguration, receiver);
    }

    default void sendChannelMessageSync(@Nonnull JsonConfiguration jsonConfiguration, @Nonnull String... receivers) {
        sendChannelMessageSync(jsonConfiguration, ErrorReportHandling.NOTHING, receivers);
    }

    void sendChannelMessageSync(@Nonnull JsonConfiguration jsonConfiguration, @Nonnull ErrorReportHandling errorReportHandling, @Nonnull String... receivers);
}
