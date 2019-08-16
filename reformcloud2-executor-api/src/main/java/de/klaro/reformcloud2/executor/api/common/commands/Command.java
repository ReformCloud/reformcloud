package de.klaro.reformcloud2.executor.api.common.commands;

import de.klaro.reformcloud2.executor.api.common.commands.complete.TabCompleter;
import de.klaro.reformcloud2.executor.api.common.commands.permission.Permission;
import de.klaro.reformcloud2.executor.api.common.commands.source.CommandSource;

import java.util.List;

public interface Command extends TabCompleter {

    String mainCommand();

    Permission permission();

    List<String> aliases();

    String description();

    AllowedCommandSources sources();

    boolean handleCommand(CommandSource commandSource, String[] strings);
}
