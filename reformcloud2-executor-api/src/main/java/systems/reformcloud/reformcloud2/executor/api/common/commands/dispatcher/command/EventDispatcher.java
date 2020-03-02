package systems.reformcloud.reformcloud2.executor.api.common.commands.dispatcher.command;

import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EventDispatcher {

    /**
     * Dispatched the command event
     *
     * @param commandEvent The command event which should get executed
     * @param command      The command which is affected by the command event
     * @return The affected command
     */
    @Nullable
    Command dispatchCommandEvent(@Nonnull CommandEvent commandEvent, @Nullable Command command);

    /**
     * Dispatched the command event
     *
     * @param commandEvent The command event which should get executed
     * @param command      The command which is affected by the command event
     * @param update       The command which should get updated
     * @return The affected command
     */
    @Nullable
    Command dispatchCommandEvent(
            @Nonnull CommandEvent commandEvent,
            @Nonnull Command command,
            @Nonnull Command update
    );

    /**
     * Dispatched the command event
     *
     * @param commandEvent The command event which should get executed
     * @param command      The command which is affected by the command event
     * @param update       The command which should get updated
     * @param line         The line which is affected by the command line
     * @return The affected command
     */
    @Nullable
    Command dispatchCommandEvent(
            @Nonnull CommandEvent commandEvent,
            @Nullable Command command,
            @Nullable Command update,
            @Nonnull String line
    );
}
