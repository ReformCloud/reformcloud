package systems.reformcloud.reformcloud2.executor.api.common.commands;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.complete.TabCompleter;
import systems.reformcloud.reformcloud2.executor.api.common.commands.manager.CommandManager;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.Permission;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

import java.util.List;

/**
 * Represents any command
 *
 * @see CommandManager#register(Command)
 * @see CommandManager#findCommand(String)
 */
public interface Command extends TabCompleter {

    TypeToken<GlobalCommand> TYPE = new TypeToken<GlobalCommand>() {
    };

    /**
     * @return The main command name
     */
    @NotNull
    String mainCommand();

    /**
     * @return The permission of the command
     */
    @Nullable
    Permission permission();

    /**
     * @return The command aliases
     */
    @NotNull
    List<String> aliases();

    /**
     * @return The command description
     */
    @NotNull
    String description();

    /**
     * @return The allowed command sources
     */
    @NotNull
    AllowedCommandSources sources();

    /**
     * Describes the command to the command sender
     *
     * @param source The source who sent the command
     */
    void describeCommandToSender(@NotNull CommandSource source);

    /**
     * Handles the command
     *
     * @param commandSource The command source of the command
     * @param strings       The parameters given in the command
     * @return If the command execute was successful
     */
    boolean handleCommand(@NotNull CommandSource commandSource, @NotNull String[] strings);
}
