package systems.reformcloud.reformcloud2.executor.api.common.commands.map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.dispatcher.CommandDispatcher;

public interface CommandMap extends CommandDispatcher {

    /**
     * Unregisters all commands
     */
    void unregisterAll();

    /**
     * Get a specific command
     *
     * @param command The command name
     * @return The command instance or {@code null} if the command is not registered
     */
    @Nullable
    Command getCommand(@NotNull String command);

    /**
     * Tries to find a command
     *
     * @param commandPreLine The known first letters of the command
     * @return The command instance or {@code null} if the command is not registered
     */
    @Nullable
    Command findCommand(@NotNull String commandPreLine);

    /**
     * Registers an command
     *
     * @param noPermissionMessage The no permission message for the command
     * @param command             The command which should get registered
     */
    void register(@NotNull String noPermissionMessage, @NotNull Command command);
}
