package systems.reformcloud.reformcloud2.executor.api.common.commands;

import com.google.gson.reflect.TypeToken;
import systems.reformcloud.reformcloud2.executor.api.common.commands.basic.GlobalCommand;
import systems.reformcloud.reformcloud2.executor.api.common.commands.complete.TabCompleter;
import systems.reformcloud.reformcloud2.executor.api.common.commands.permission.Permission;
import systems.reformcloud.reformcloud2.executor.api.common.commands.source.CommandSource;

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
