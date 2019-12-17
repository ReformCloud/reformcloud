package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

import javax.annotation.Nonnull;
import java.util.Arrays;

public final class CommandStop extends GlobalCommand {

    public CommandStop() {
        super("stop", "reformcloud.command.permissions", "The stop command", Arrays.asList(
                "exit", "close", "end"
        ));
    }

    @Override
    public boolean handleCommand(@Nonnull CommandSource commandSource, @Nonnull String[] strings) {
        System.exit(0);
        return true;
    }
}
