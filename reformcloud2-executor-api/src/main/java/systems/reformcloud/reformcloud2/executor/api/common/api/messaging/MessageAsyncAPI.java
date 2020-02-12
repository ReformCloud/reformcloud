package systems.reformcloud.reformcloud2.executor.api.common.api.messaging;

import systems.reformcloud.reformcloud2.executor.api.common.api.messaging.util.ErrorReportHandling;
import systems.reformcloud.reformcloud2.executor.api.common.configuration.JsonConfiguration;
import systems.reformcloud.reformcloud2.executor.api.common.utility.task.Task;

import javax.annotation.Nonnull;

public interface MessageAsyncAPI {

    @Nonnull
    default Task<Void> sendChannelMessageAsync(@Nonnull String receiver, @Nonnull JsonConfiguration jsonConfiguration) {
        return sendChannelMessageAsync(jsonConfiguration, receiver);
    }

    @Nonnull
    default Task<Void> sendChannelMessageAsync(@Nonnull JsonConfiguration jsonConfiguration, @Nonnull String... receivers) {
        return sendChannelMessageAsync(jsonConfiguration, ErrorReportHandling.NOTHING, receivers);
    }

    @Nonnull
    Task<Void> sendChannelMessageAsync(@Nonnull JsonConfiguration jsonConfiguration, @Nonnull ErrorReportHandling errorReportHandling, @Nonnull String... receivers);
}
