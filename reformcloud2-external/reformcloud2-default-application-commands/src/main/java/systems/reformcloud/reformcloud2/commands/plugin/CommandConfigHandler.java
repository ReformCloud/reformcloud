package systems.reformcloud.reformcloud2.commands.plugin;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.reformcloud2.commands.config.CommandsConfig;
import systems.reformcloud.reformcloud2.executor.api.common.base.Conditions;

public abstract class CommandConfigHandler {

    private static CommandConfigHandler instance;

    public static CommandConfigHandler getInstance() {
        return instance;
    }

    public static void setInstance(@NotNull CommandConfigHandler handler) {
        Conditions.isTrue(instance == null);
        instance = handler;
    }

    public abstract void handleCommandConfigRelease(@NotNull CommandsConfig commandsConfig);

    public abstract void unregisterAllCommands();
}
