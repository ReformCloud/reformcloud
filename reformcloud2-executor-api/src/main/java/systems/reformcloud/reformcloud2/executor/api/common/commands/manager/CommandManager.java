package systems.reformcloud.reformcloud2.executor.api.common.commands.manager;

import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;
import systems.reformcloud.reformcloud2.executor.api.common.commands.map.CommandMap;

import java.util.List;

public interface CommandManager extends CommandMap {

    CommandManager register(Command command);

    CommandManager register(Class<? extends Command> command);

    void unregisterCommand(Command command);

    Command unregisterAndGetCommand(String command);

    List<Command> getCommands();
}
