package de.klaro.reformcloud2.executor.api.common.commands;

import com.google.gson.reflect.TypeToken;
import de.klaro.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import de.klaro.reformcloud2.executor.api.common.commands.complete.TabCompleter;
import de.klaro.reformcloud2.executor.api.common.commands.permission.Permission;
import de.klaro.reformcloud2.executor.api.common.commands.source.CommandSource;

import java.util.List;

public interface Command extends TabCompleter {

    TypeToken<GlobalCommand> TYPE = new TypeToken<GlobalCommand>() {};

    String mainCommand();

    Permission permission();

    List<String> aliases();

    String description();

    AllowedCommandSources sources();

    boolean handleCommand(CommandSource commandSource, String[] strings);
}
