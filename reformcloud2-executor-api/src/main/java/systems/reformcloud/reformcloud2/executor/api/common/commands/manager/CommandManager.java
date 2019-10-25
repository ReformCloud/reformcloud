package systems.reformcloud.reformcloud2.executor.api.common.commands.manager;

import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.map.CommandMap;

import java.util.List;

public interface CommandManager extends CommandMap {

    /**
     * Registers a command
     *
     * @param command The command which should be registered
     * @return The current instance of the command manager
     */
    CommandManager register(Command command);

    /**
     * Registers a command
     *
     * @param command The command-class which should be instantiated and then registered
     * @return The current instance of the command manager
     */
    CommandManager register(Class<? extends Command> command);

    /**
     * Unregisters a command
     *
     * @param command The command instance which should get unregistered
     */
    void unregisterCommand(Command command);

    /**
     * Unregisters a command
     *
     * @param command The command name which should get unregistered
     * @return The last known command instance
     */
    Command unregisterAndGetCommand(String command);

    /**
     * @return All registered commands
     */
    List<Command> getCommands();
}
