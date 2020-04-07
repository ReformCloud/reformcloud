package systems.reformcloud.reformcloud2.executor.api.common.commands.basic.command.sources;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.executor.api.common.commands.AllowedCommandSources;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;

import java.util.List;

public abstract class ApiCommand extends GlobalCommand {

    public ApiCommand(String command, String permission, String description, List<String> aliases) {
        super(command, permission, description, aliases);
    }

    public ApiCommand(String command, String description, List<String> aliases) {
        super(command, description, aliases);
    }

    public ApiCommand(String command, String description) {
        super(command, description);
    }

    public ApiCommand(String command) {
        super(command);
    }

    @NotNull
    @Override
    public AllowedCommandSources sources() {
        return AllowedCommandSources.API;
    }
}
