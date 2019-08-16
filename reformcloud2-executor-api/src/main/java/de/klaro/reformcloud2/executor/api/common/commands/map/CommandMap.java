package de.klaro.reformcloud2.executor.api.common.commands.map;

import de.klaro.reformcloud2.executor.api.common.commands.Command;
import de.klaro.reformcloud2.executor.api.common.commands.dispatcher.CommandDispatcher;

public interface CommandMap extends CommandDispatcher {

    void unregisterAll();

    Command getCommand(String command);

    Command findCommand(String commandPreLine);

    void register(String noPermissionMessage, Command command);
}
