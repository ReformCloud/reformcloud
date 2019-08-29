package de.klaro.reformcloud2.executor.api.common.commands.basic.commands;

import de.klaro.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import de.klaro.reformcloud2.executor.api.common.commands.basic.command.sources.ConsoleCommand;
import de.klaro.reformcloud2.executor.api.common.commands.source.CommandSource;
import de.klaro.reformcloud2.executor.api.common.utility.runtime.ReloadableRuntime;

import java.util.Arrays;

public final class CommandReload extends ConsoleCommand {


    public CommandReload(ReloadableRuntime reloadableRuntime) {
        super("reload", null, GlobalCommand.DEFAULT_DESCRIPTION, Arrays.asList(
                "rl"
        ));

        this.reloadableRuntime = reloadableRuntime;
    }

    private final ReloadableRuntime reloadableRuntime;

    @Override
    public boolean handleCommand(CommandSource commandSource, String[] strings) {
        try {
            reloadableRuntime.reload();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }
}