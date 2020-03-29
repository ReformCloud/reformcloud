package systems.reformcloud.reformcloud2.executor.api.common.api.messaging;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ReceiverType;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;

public interface MessageSyncAPI {

    /**
     * Sends a channel message to the specified sender
     *
     * @param receiver          The receiver of the channel message
     * @param jsonConfiguration The content which should get sent
     */
    default void sendChannelMessageSync(@NotNull String receiver, @NotNull JsonConfiguration jsonConfiguration) {
        sendChannelMessageSync(jsonConfiguration, ErrorReportHandling.NOTHING, receiver);
    }

    /**
     * Sends a channel message to a bunch of receivers
     *
     * @param jsonConfiguration The content which should get sent
     * @param receivers         The receivers of the channel message
     */
    default void sendChannelMessageSync(@NotNull JsonConfiguration jsonConfiguration, @NotNull String... receivers) {
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
    default void sendChannelMessageSync(@NotNull JsonConfiguration jsonConfiguration,
                                        @NotNull ErrorReportHandling errorReportHandling,
                                        @NotNull String... receivers) {
        sendChannelMessageSync(jsonConfiguration, "unknown", errorReportHandling, receivers);
    }

    /**
     * Sends a channel message to a bunch of receivers and specifies the error handling if a specified
     * receiver is not available in the network
     *
     * @param jsonConfiguration   The content which should get sent
     * @param baseChannel         The base channel through which the message got sent
     * @param errorReportHandling The handling type of errors if a channel is not available in the network
     * @param receivers           The receivers of the channel message
     */
    default void sendChannelMessageSync(
            @NotNull JsonConfiguration jsonConfiguration,
            @NotNull String baseChannel,
            @NotNull ErrorReportHandling errorReportHandling,
            @NotNull String... receivers) {
        sendChannelMessageSync(jsonConfiguration, baseChannel, "unknown", errorReportHandling, receivers);
    }

    /**
     * Sends a channel message to a bunch of receivers and specifies the error handling if a specified
     * receiver is not available in the network
     *
     * @param jsonConfiguration   The content which should get sent
     * @param baseChannel         The base channel through which the message got sent
     * @param subChannel          The channel identifier through which the message comes
     * @param errorReportHandling The handling type of errors if a channel is not available in the network
     * @param receivers           The receivers of the channel message
     */
    void sendChannelMessageSync(
            @NotNull JsonConfiguration jsonConfiguration,
            @NotNull String baseChannel,
            @NotNull String subChannel,
            @NotNull ErrorReportHandling errorReportHandling,
            @NotNull String... receivers
    );

    /**
     * Sends a channel message to all receivers which equals to the receiver type filter
     *
     * @param configuration The content which should get sent
     * @param receiverTypes The type of receivers which should get the message
     */
    default void sendChannelMessageSync(@NotNull JsonConfiguration configuration, @NotNull ReceiverType... receiverTypes) {
        sendChannelMessageSync(configuration, "unknown", receiverTypes);
    }

    /**
     * Sends a channel message to all receivers which equals to the receiver type filter
     *
     * @param configuration The content which should get sent
     * @param baseChannel   The base channel through which the message got sent
     * @param receiverTypes The type of receivers which should get the message
     */
    default void sendChannelMessageSync(@NotNull JsonConfiguration configuration,
                                        @NotNull String baseChannel,
                                        @NotNull ReceiverType... receiverTypes) {
        sendChannelMessageSync(configuration, baseChannel, "unknown", receiverTypes);
    }

    /**
     * Sends a channel message to all receivers which equals to the receiver type filter
     *
     * @param configuration The content which should get sent
     * @param baseChannel   The base channel through which the message got sent
     * @param subChannel    The channel identifier through which the message comes
     * @param receiverTypes The type of receivers which should get the message
     */
    void sendChannelMessageSync(@NotNull JsonConfiguration configuration, @NotNull String baseChannel,
                                @NotNull String subChannel, @NotNull ReceiverType... receiverTypes);
}
