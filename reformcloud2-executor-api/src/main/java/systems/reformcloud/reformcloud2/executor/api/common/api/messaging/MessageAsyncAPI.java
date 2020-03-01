package systems.reformcloud.reformcloud2.executor.api.common.api.messaging;

import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ReceiverType;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.Nonnull;

public interface MessageAsyncAPI {

    /**
     * Sends a channel message to the specified sender
     *
     * @param receiver          The receiver of the channel message
     * @param jsonConfiguration The content which should get sent
     * @return A task which will get completed when the message got sent to all receivers
     */
    @Nonnull
    default Task<Void> sendChannelMessageAsync(@Nonnull String receiver, @Nonnull JsonConfiguration jsonConfiguration) {
        return sendChannelMessageAsync(jsonConfiguration, ErrorReportHandling.NOTHING, receiver);
    }

    /**
     * Sends a channel message to a bunch of receivers
     *
     * @param jsonConfiguration The content which should get sent
     * @param receivers         The receivers of the channel message
     * @return A task which will get completed when the message got sent to all receivers
     */
    @Nonnull
    default Task<Void> sendChannelMessageAsync(@Nonnull JsonConfiguration jsonConfiguration, @Nonnull String... receivers) {
        return sendChannelMessageAsync(jsonConfiguration, ErrorReportHandling.NOTHING, receivers);
    }

    /**
     * Sends a channel message to a bunch of receivers and specifies the error handling if a specified
     * receiver is not available in the network
     *
     * @param jsonConfiguration   The content which should get sent
     * @param errorReportHandling The handling type of errors if a channel is not available in the network
     * @param receivers           The receivers of the channel message
     * @return A task which will get completed when the message got sent to all receivers
     */
    @Nonnull
    default Task<Void> sendChannelMessageAsync(@Nonnull JsonConfiguration jsonConfiguration,
                                               @Nonnull ErrorReportHandling errorReportHandling,
                                               @Nonnull String... receivers) {
        return sendChannelMessageAsync(jsonConfiguration, "unknown", errorReportHandling, receivers);
    }

    /**
     * Sends a channel message to a bunch of receivers and specifies the error handling if a specified
     * receiver is not available in the network
     *
     * @param jsonConfiguration   The content which should get sent
     * @param baseChannel         The base channel through which the message got sent
     * @param errorReportHandling The handling type of errors if a channel is not available in the network
     * @param receivers           The receivers of the channel message
     * @return A task which will get completed when the message got sent to all receivers
     */
    @Nonnull
    default Task<Void> sendChannelMessageAsync(
            @Nonnull JsonConfiguration jsonConfiguration,
            @Nonnull String baseChannel,
            @Nonnull ErrorReportHandling errorReportHandling,
            @Nonnull String... receivers) {
        return sendChannelMessageAsync(jsonConfiguration, baseChannel, "unknown", errorReportHandling, receivers);
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
     * @return A task which will get completed when the message got sent to all receivers
     */
    @Nonnull
    Task<Void> sendChannelMessageAsync(
            @Nonnull JsonConfiguration jsonConfiguration,
            @Nonnull String baseChannel,
            @Nonnull String subChannel,
            @Nonnull ErrorReportHandling errorReportHandling,
            @Nonnull String... receivers
    );

    /**
     * Sends a channel message to all receivers which equals to the receiver type filter
     *
     * @param configuration The content which should get sent
     * @param receiverTypes The type of receivers which should get the message
     * @return A task which will get completed when the message got sent to all receivers
     */
    @Nonnull
    default Task<Void> sendChannelMessageAsync(@Nonnull JsonConfiguration configuration, @Nonnull ReceiverType... receiverTypes) {
        return sendChannelMessageAsync(configuration, "unknown", receiverTypes);
    }

    /**
     * Sends a channel message to all receivers which equals to the receiver type filter
     *
     * @param configuration The content which should get sent
     * @param baseChannel   The base channel through which the message got sent
     * @param receiverTypes The type of receivers which should get the message
     * @return A task which will get completed when the message got sent to all receivers
     */
    @Nonnull
    default Task<Void> sendChannelMessageAsync(@Nonnull JsonConfiguration configuration,
                                               @Nonnull String baseChannel,
                                               @Nonnull ReceiverType... receiverTypes) {
        return sendChannelMessageAsync(configuration, baseChannel, "unknown", receiverTypes);
    }

    /**
     * Sends a channel message to all receivers which equals to the receiver type filter
     *
     * @param configuration The content which should get sent
     * @param baseChannel   The base channel through which the message got sent
     * @param subChannel    The channel identifier through which the message comes
     * @param receiverTypes The type of receivers which should get the message
     * @return A task which will get completed when the message got sent to all receivers
     */
    @Nonnull
    Task<Void> sendChannelMessageAsync(@Nonnull JsonConfiguration configuration, @Nonnull String baseChannel,
                                       @Nonnull String subChannel, @Nonnull ReceiverType... receiverTypes);
}
