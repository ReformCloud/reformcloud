package systems.reformcloud.reformcloud2.executor.api.common.commands.dispatcher.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;

public interface EventDispatcher {

    /**
     * Dispatched the command event
     *
     * @param commandEvent The command event which should get executed
     * @param command      The command which is affected by the command event
     * @return The affected command
     */
    @Nullable
    Command dispatchCommandEvent(@NotNull CommandEvent commandEvent, @Nullable Command command);

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
            @NotNull CommandEvent commandEvent,
            @NotNull Command command,
            @NotNull Command update
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
            @NotNull CommandEvent commandEvent,
            @Nullable Command command,
            @Nullable Command update,
            @NotNull String line
    );
}
