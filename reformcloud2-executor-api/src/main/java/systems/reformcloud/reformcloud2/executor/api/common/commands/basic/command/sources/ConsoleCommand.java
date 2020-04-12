package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.command.sources;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;

import java.util.List;

public abstract class ConsoleCommand extends GlobalCommand {

    public ConsoleCommand(String command, String permission, String description, List<String> aliases) {
        super(command, permission, description, aliases);
    }

    public ConsoleCommand(String command, String description, List<String> aliases) {
        super(command, description, aliases);
    }

    public ConsoleCommand(String command, String description) {
        super(command, description);
    }

    public ConsoleCommand(String command) {
        super(command);
    }

    @NotNull
    @Override
    public AllowedCommandSources sources() {
        return AllowedCommandSources.CONSOLE;
    }
}
