package systems.reformcloud.reformcloud2.executor.api.common.commands.dispatcher.command;

import systems.reformcloud.reformcloud2.executor.api.common.commands.Command;

public interface EventDispatcher {

    Command dispatchCommandEvent(CommandEvent commandEvent, Command command);

    Command dispatchCommandEvent(CommandEvent commandEvent, Command command, Command update);

    Command dispatchCommandEvent(CommandEvent commandEvent, Command command, Command update, String line);
}
