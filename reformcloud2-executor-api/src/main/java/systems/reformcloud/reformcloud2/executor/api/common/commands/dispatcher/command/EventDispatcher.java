package systems.reformcloud.reformcloud2.executor.api.common.commands.dispatcher.command;

import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EventDispatcher {

    @Nullable
    Command dispatchCommandEvent(
            @Nonnull CommandEvent commandEvent,
            @Nullable Command command
    );

    @Nullable
    Command dispatchCommandEvent(
            @Nonnull CommandEvent commandEvent,
            @Nonnull Command command,
            @Nonnull Command update
    );

    @Nullable
    Command dispatchCommandEvent(
            @Nonnull CommandEvent commandEvent,
            @Nullable Command command,
            @Nullable Command update,
            @Nonnull String line
    );
}
