package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;

import java.util.Collections;

public final class CommandClear extends GlobalCommand {

    public CommandClear(LoggerBase loggerBase) {
        super("clear", "reformcloud.command.clear", "Clears the console", Collections.emptyList());
        this.loggerBase = loggerBase;
    }

    private final LoggerBase loggerBase;

    @Override
    public boolean handleCommand(CommandSource commandSource, String[] strings) {
        loggerBase.clearScreen();
        return true;
    }
}
