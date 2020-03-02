package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;

import javax.annotation.Nonnull;

public final class CommandStop extends GlobalCommand {

    public CommandStop() {
        super("stop", "reformcloud.command.permissions", "The stop command", "exit", "close", "end");
    }

    @Override
    public void describeCommandToSender(@Nonnull CommandSource source) {
        source.sendMessage(LanguageManager.get("command-stop-description"));
    }

    @Override
    public boolean handleCommand(@Nonnull CommandSource commandSource, @Nonnull String[] strings) {
        System.exit(0);
        return true;
    }
}
