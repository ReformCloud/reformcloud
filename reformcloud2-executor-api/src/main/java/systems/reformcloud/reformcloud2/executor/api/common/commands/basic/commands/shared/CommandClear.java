package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;
import systems.reformcloud.reformcloud2.executor.api.common.logger.LoggerBase;

public final class CommandClear extends GlobalCommand {

    public CommandClear(LoggerBase loggerBase) {
        super("clear", "reformcloud.command.clear", "Clears the console", "cls");
        this.loggerBase = loggerBase;
    }

    private final LoggerBase loggerBase;

    @Override
    public void describeCommandToSender(@NotNull CommandSource source) {
        source.sendMessage(LanguageManager.get("command-clear-description"));
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings) {
        loggerBase.clearScreen();
        return true;
    }
}
