package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared;

import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;

import javax.annotation.Nonnull;

public final class CommandClear extends GlobalCommand {

    public CommandClear(LoggerBase loggerBase) {
        super("clear", "reformcloud.command.clear", "Clears the console", "cls");
        this.loggerBase = loggerBase;
    }

    private final LoggerBase loggerBase;

    @Override
    public void describeCommandToSender(@Nonnull CommandSource source) {
        source.sendMessage(LanguageManager.get("command-clear-description"));
    }

    @Override
    public boolean handleCommand(@Nonnull CommandSource commandSource, @Nonnull String[] strings) {
        loggerBase.clearScreen();
        return true;
    }
}
