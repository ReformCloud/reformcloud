package systems.reformcloud.reformcloud2.executor.api.common.api.messaging;

import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

import javax.annotation.Nonnull;

public interface MessageSyncAPI {

    /**
     * Sends a channel message to the specified sender
     *
     * @param receiver          The receiver of the channel message
     * @param jsonConfiguration The content which should get sent
     */
    default void sendChannelMessageSync(@Nonnull String receiver, @Nonnull JsonConfiguration jsonConfiguration) {
        sendChannelMessageSync(jsonConfiguration, receiver);
    }

    /**
     * Sends a channel message to a bunch of receivers
     *
     * @param jsonConfiguration The content which should get sent
     * @param receivers         The receivers of the channel message
     */
    default void sendChannelMessageSync(@Nonnull JsonConfiguration jsonConfiguration, @Nonnull String... receivers) {
        sendChannelMessageSync(jsonConfiguration, ErrorReportHandling.NOTHING, receivers);
    }

    /**
     * Sends a channel message to a bunch of receivers and specifies the error handling if a specified
     * receiver is not available in the network
     *
     * @param jsonConfiguration   The content which should get sent
     * @param errorReportHandling The handling type of errors if a channel is not available in the network
     * @param receivers           The receivers of the channel message
     */
    void sendChannelMessageSync(@Nonnull JsonConfiguration jsonConfiguration, @Nonnull ErrorReportHandling errorReportHandling, @Nonnull String... receivers);
}
