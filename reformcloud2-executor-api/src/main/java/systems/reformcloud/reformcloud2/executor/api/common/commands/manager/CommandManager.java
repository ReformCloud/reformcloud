package systems.reformcloud.reformcloud2.executor.api.common.commands.manager;

import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.map.CommandMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface CommandManager extends CommandMap {

    /**
     * Registers a command
     *
     * @param command The command which should be registered
     * @return The current instance of the command manager
     */
    @Nonnull
    CommandManager register(@Nonnull Command command);

    /**
     * Registers a command
     *
     * @param command The command-class which should be instantiated and then registered
     * @return The current instance of the command manager
     */
    @Nonnull
    CommandManager register(@Nonnull Class<? extends Command> command);

    /**
     * Unregisters a command
     *
     * @param command The command instance which should get unregistered
     */
    void unregisterCommand(@Nonnull Command command);

    /**
     * Unregisters a command
     *
     * @param command The command name which should get unregistered
     * @return The last known command instance
     */
    @Nullable
    Command unregisterAndGetCommand(@Nonnull String command);

    /**
     * @return All registered commands
     */
    @Nonnull
    List<Command> getCommands();
}
