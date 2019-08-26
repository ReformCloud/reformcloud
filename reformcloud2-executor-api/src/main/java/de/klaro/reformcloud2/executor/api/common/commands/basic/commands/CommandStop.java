package de.klaro.reformcloud2.executor.api.common.commands.basic.commands;

import de.klaro.reformcloud2.executor.api.common.commands.basic.command.sources.ConsoleCommand;
import de.klaro.reformcloud2.executor.api.common.commands.source.CommandSource;

import java.util.Arrays;

public final class CommandStop extends ConsoleCommand {

    public CommandStop() {
        super("stop", "reformcloud.command.permissions", "The stop command", Arrays.asList(
                "exit", "close", "end"
        ));
    }

    @Override
    public boolean handleCommand(CommandSource commandSource, String[] strings) {
        System.exit(0);
        return true;
    }
}
