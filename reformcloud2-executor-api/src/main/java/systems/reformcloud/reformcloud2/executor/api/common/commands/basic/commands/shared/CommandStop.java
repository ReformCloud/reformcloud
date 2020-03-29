package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands.shared;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;
import systems.reformcloud.reformcloud2.executor.api.common.language.LanguageManager;

public final class CommandStop extends GlobalCommand {

    public CommandStop() {
        super("stop", "reformcloud.command.permissions", "The stop command", "exit", "close", "end");
    }

    @Override
    public void describeCommandToSender(@NotNull CommandSource source) {
        source.sendMessage(LanguageManager.get("command-stop-description"));
    }

    @Override
    public boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings) {
        System.exit(0);
        return true;
    }
}
