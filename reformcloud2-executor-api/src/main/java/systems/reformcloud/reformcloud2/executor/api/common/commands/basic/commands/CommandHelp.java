package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.commands;

import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

import javax.annotation.Nonnull;
import java.util.Arrays;

public final class CommandHelp extends GlobalCommand {

    public CommandHelp(CommandManager commandManager) {
        super("help", null, GlobalCommand.DEFAULT_DESCRIPTION, Arrays.asList(
                "ask", "?"
        ));

        this.commandManager = commandManager;
    }

    private final CommandManager commandManager;

    @Override
    public boolean handleCommand(@Nonnull CommandSource commandSource, @Nonnull String[] strings) {
        commandSource.sendMessage("ReformCloud git:runner:"
                + System.getProperty("reformcloud.runner.version", "c-build")
                + ":"
                + CommandHelp.class.getPackage().getSpecificationVersion()
                + " by derklaro and ReformCloud-Community"
        );
        commandSource.sendMessage(" ");

        commandManager.getCommands().forEach(command -> commandSource.sendMessage("   -> " + command.mainCommand() + " " + command.aliases()));
        return true;
    }
}
