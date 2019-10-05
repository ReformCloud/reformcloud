package systems.reformcloud.reformcloud2.executor.api.common.commands.dispatcher;

import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.dispatcher.command.EventDispatcher;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

import java.util.function.Consumer;

public interface CommandDispatcher extends EventDispatcher {

    void dispatchCommand(CommandSource commandSource, AllowedCommandSources commandSources, String commandLine, Consumer<String> result);
}
