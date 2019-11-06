package systems.reformcloud.reformcloud2.executor.api.common.commands.map;

import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.dispatcher.CommandDispatcher;

import javax.annotation.Nullable;

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
    Command getCommand(String command);

    /**
     * Tries to find a command
     *
     * @param commandPreLine The known first letters of the command
     * @return The command instance or {@code null} if the command is not registered
     */
    @Nullable
    Command findCommand(String commandPreLine);

    /**
     * Registers an command
     *
     * @param noPermissionMessage The no permission message for the command
     * @param command The command which should get registered
     */
    void register(String noPermissionMessage, Command command);
}
