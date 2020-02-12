package systems.reformcloud.reformcloud2.executor.api.common.api.messaging;

import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
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
        return sendChannelMessageAsync(jsonConfiguration, receiver);
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
    Task<Void> sendChannelMessageAsync(@Nonnull JsonConfiguration jsonConfiguration, @Nonnull ErrorReportHandling errorReportHandling, @Nonnull String... receivers);
}
