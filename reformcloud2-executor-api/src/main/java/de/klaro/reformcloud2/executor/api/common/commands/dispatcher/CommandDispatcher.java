package de.klaro.reformcloud2.executor.api.common.commands.dispatcher;

import de.klaro.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import de.klaro.reformcloud2.executor.api.common.commands.dispatcher.command.EventDispatcher;
import de.klaro.reformcloud2.executor.api.common.commands.source.CommandSource;

import java.util.function.Consumer;

public interface CommandDispatcher extends EventDispatcher {

    void dispatchCommand(CommandSource commandSource, AllowedCommandSources commandSources, String commandLine, Consumer<String> result);
}
